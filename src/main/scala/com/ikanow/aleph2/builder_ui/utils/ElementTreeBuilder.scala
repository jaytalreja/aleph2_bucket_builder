/*******************************************************************************
* Copyright 2015, The IKANOW Open Source Project.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package com.ikanow.aleph2.builder_ui.utils

import com.ikanow.aleph2.builder_ui.data_model._

import com.greencatsoft.angularjs.core._
import scala.scalajs.js
import scala.scalajs.js.JSON
import com.greencatsoft.angularjs._

import js.JSConverters._
import scala.collection.mutable.MutableList

import js.JSConverters._

/** Utilities to build the element and element template trees
 * @author alex
 */
object ElementTreeBuilder {
  
    /** Filters the templates and converts to a category-template "tree" (2 levels)
     * @param breadcrumb
     * @param templates
     * @return
     */
    def getTemplateTree(breadcrumb: js.Array[String], templates: js.Array[ElementTemplateJs]): js.Array[ElementTemplateNodeJs] = {
      val breadcrumb_str = breadcrumb.mkString("/")
      
      templates
        .zipWithIndex
        // Step 1, build a map of beans against their categories
        .filter { case (bean, index) => !bean.filters
                            .filter { path => path.equals(breadcrumb_str) }.isEmpty 
         }
        .flatMap { case (bean, index) => bean.categories.map { cat => (cat, (bean, index)) } }
        .groupBy(_._1)
        .mapValues(_.map(_._2).sortBy { case (bean, index) => bean.display_name })
        // Step 2, convert to a simple tree
        .map { case (category, bean_list) => 
              ElementTemplateNodeJs(-1, category,
                  bean_list.map { case (bean, index) => ElementTemplateNodeJs(index, bean.display_name) }.toJSArray
                  )
              }.toJSArray
    }  
    
    /** Add the $parent attribute to a tree that's been imported from JSON
     * @param root
     * @return
     */
    def fillInImportedTree(root: ElementNodeJs): ElementNodeJs = {
      def recursive(root: ElementNodeJs, parent: ElementNodeJs):Unit = {
        root.$parent = parent
        if (null != root.children)
          root.children.foreach { child => recursive(child, root) }        
      }
      recursive(root, null)
      root
    }
    
    /** Removes all the JS-specific parameters and stringify
     * @param root
     * @return
     */
    def stringifyTree(root: ElementNodeJs): String = {
        JSON.stringify(root, (k: String, v: js.Any) => {
          if ((null == v) || (k.startsWith("$") && !k.equals("$fn")))
            js.undefined.asInstanceOf[js.Any]
          else
            v
        })
    }
    
    /** Recursive builder method for the final JSON
     * @param curr_template
     * @param root_template
     * @param mutable_curr_output
     * @param mutable_root_output
     * @param hierarchy
     * @param rows
     * @param cols
     */
    def generateOutput(curr_template: ElementNodeJs, root_template: ElementNodeJs, 
                      mutable_curr_output: js.Any, mutable_root_output: js.Dictionary[js.Any], 
                      hierarchy: List[ElementNodeJs], rows: Seq[ElementNodeJs], cols: Seq[ElementNodeJs],
                      mutable_errs: MutableList[Tuple2[String, ElementNodeJs]]
    ): Unit = {
      // Step 0: apply myself
      
      val maybe_new_obj = if (!curr_template.root) {
        
        val validation_passed = callBuilder(true,
          Option(curr_template.element.template.validation_function)
            .filter { f => !js.isUndefined(f) }
            .flatMap { f => f.get("$fn") },
          mutable_errs,
          curr_template,
          mutable_curr_output,
          root_template,
          mutable_root_output,
          hierarchy, rows, cols)
          .map { any => !any.equals(false) }
          .getOrElse(true)                  
          
        if (!validation_passed) {
          return
        }
          
        val errs1 = mutable_errs.size
          
        val maybe_new_obj_int: Option[js.Any] = callBuilder(false,
          Option(curr_template.element.template.building_function)
            .filter { f => !js.isUndefined(f) }
             .flatMap { f => f.get("$fn") },
          mutable_errs,
          curr_template,
          mutable_curr_output,
          root_template,
          mutable_root_output,
          hierarchy, rows, cols)
        
        val errs2 = mutable_errs.size
      
        if (errs1 != errs2) {// emergency bail out
          return
        }
            
      //DEBUG
//        mutable_curr_output match {
//          case array: js.Array[_] => array.asInstanceOf[js.Array[js.Any]]
//                                      .append(js.Dynamic.literal(
//                                          name = curr_template.label + "_pre",
//                                          hierarchy = hierarchy.map { r => r.label }.toString(),
//                                          rows = rows.map { r => r.label }.toString(),
//                                          cols = cols.map { r => r.label }.toString()
//                                          ).asInstanceOf[js.Any])
//        }
        
        maybe_new_obj_int
      }
      else Option.empty
      
      val new_hierarchy = 
        if (curr_template.root) 
          hierarchy 
        else
          curr_template :: hierarchy; 
      
      // Step 1: sort the children
      
      Option(curr_template.children).foreach { children => {
        var sorted_children = curr_template.children.sortBy { node => (node.element.col, node.element.row) }      
        sorted_children.foreach { child => {
          
          // Step 2: recurse through
          
          val new_rows = getRows(curr_template, child)
          val new_cols = getCols(curr_template, child)
          
          generateOutput(child, root_template, maybe_new_obj.getOrElse(mutable_curr_output), mutable_root_output,
              new_hierarchy, new_rows, new_cols, mutable_errs) }
        }}
      }
      
      // Step 4: post application
      
      if (!curr_template.root) {
        
        callBuilder(true,
          Option(curr_template.element.template.post_validation_function)
            .filter { f => !js.isUndefined(f) }
            .flatMap { f => f.get("$fn") },
          mutable_errs,
          curr_template,
          mutable_curr_output,
          root_template,
          mutable_root_output,
          hierarchy, rows, cols)
          
      // (ignore result)
          
        callBuilder(false,
          Option(curr_template.element.template.post_building_function)
            .filter { f => !js.isUndefined(f) }
            .flatMap { f => f.get("$fn") },
          mutable_errs,
          curr_template,
          mutable_curr_output,
          root_template,
          mutable_root_output,
          hierarchy, rows, cols)

      // (ignore result)
          
      //DEBUG
//        mutable_curr_output match {
//          case array: js.Array[_] => array.asInstanceOf[js.Array[js.Any]]
//                                      .append(js.Dynamic.literal(name = curr_template.label + "_post").asInstanceOf[js.Any])
//        }
      }
    }
    
    protected def getRows(parent: ElementNodeJs, child: ElementNodeJs): Seq[ElementNodeJs] = {
      // starts before or on the same row
      // after applying the span (-1) then ends after or on the same row
      parent.children.filter { other => (other.element.row <= child.element.row) && 
                                       ((other.element.row + (other.element.sizeY-1)) >= child.element.row) }
                     .filter { other => (other.element.row != child.element.row) ||  (other.element.col != child.element.col) }
    }
    protected def getCols(parent: ElementNodeJs, child: ElementNodeJs): Seq[ElementNodeJs] = {
      // starts before or on the same col
      // after applying the span (-1) then ends after or on the same col
      parent.children.filter { other => (other.element.col <= child.element.col) && 
                                       ((other.element.col + (other.element.sizeX-1)) >= child.element.col) }
                     .filter { other => (other.element.row != child.element.row) ||  (other.element.col != child.element.col) }
    }

    // User function will be
    // mutable_errors
    // card
    // object
    // full_builder
    // full_json
    // hierarchy, rows, cols    
    // ... and returns either null/undef or an object (if the plan is to "Step into" that method)
    
    protected def callBuilder(validation_only: Boolean,
        maybe_function_str: Option[String],
        errs: MutableList[Tuple2[String, ElementNodeJs]],
        curr_node: ElementNodeJs,
        curr_obj: js.Any,
        full_builder: ElementNodeJs,
        full_json: js.Dictionary[js.Any],
        hierarchy: List[ElementNodeJs], rows: Seq[ElementNodeJs], cols: Seq[ElementNodeJs]
        
    ): Option[js.Any] = {
      
      try { 
        maybe_function_str.map { fn_str => {
          
          val fn: js.Function8[js.Array[String], //errors
            ElementNodeJs, // card
            js.Any, // object
            ElementNodeJs, // full_builder
            js.Dictionary[js.Any], // root object
            js.Array[ElementNodeJs], js.Array[ElementNodeJs], js.Array[ElementNodeJs], //hierachy, rows, cols
            js.Any // return val
          ]          
          = js.eval("temp = " + fn_str).asInstanceOf[js.Function8[js.Array[String], ElementNodeJs, js.Any, ElementNodeJs, js.Dictionary[js.Any], 
            js.Array[ElementNodeJs], js.Array[ElementNodeJs], js.Array[ElementNodeJs], js.Any]]
            
          val tmp_errs: js.Array[String] = js.Array()

          val retval = fn.apply(tmp_errs, curr_node, curr_obj, full_builder, full_json, hierarchy.toJSArray, rows.toJSArray, cols.toJSArray)
          
          errs ++= tmp_errs.map { err => (err,  curr_node) }
          
          retval
        }}
        .filter { retval => !js.isUndefined(retval) }
      }
      catch {
        case e: Exception => {
          //DEBUG
          e.printStackTrace()          
          errs += Tuple2("EXCEPTION: " + e.toString() + ": " + e.getStackTrace()(0).toString() , curr_node); 
        }
        if (validation_only) 
          Option(false)
        else
          Option.empty          
      }
    }
}

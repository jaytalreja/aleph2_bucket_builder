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
    
    def generateOutput(root: ElementNodeJs, mutable_output: js.Dictionary[js.Any], hierarchy: Seq[js.Any]): Unit = {
      // Step 0: apply myself
      
      hierarchy.take(1).foreach { curr_object => curr_object match {
      //TODO (example code) .. call fn, add to hierarchy etc
        case array: js.Array[_] => array.asInstanceOf[js.Array[js.Any]]
                                    .append(js.Dynamic.literal(name = root.label + "_pre").asInstanceOf[js.Any])
        }
      }
      
      // Step 1: sort the children
      
      Option(root.children).foreach { children => {
        var sorted_children = root.children.sortBy { node => (node.element.col, node.element.row) }      
        sorted_children.foreach { child => generateOutput(child, mutable_output, hierarchy) }
      }}
      
      hierarchy.take(1).foreach { curr_object => curr_object match {
      //TODO (example code) .. call fn, add to hierarchy etc
        case array: js.Array[_] => array.asInstanceOf[js.Array[js.Any]]
                                    .append(js.Dynamic.literal(name = root.label + "_post").asInstanceOf[js.Any])
        }
      }      
    }
}

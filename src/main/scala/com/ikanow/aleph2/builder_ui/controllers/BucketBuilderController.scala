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
package com.ikanow.aleph2.builder_ui.controllers

import com.greencatsoft.angularjs.core._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLElement

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.JSApp
import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.extensions._

import ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.annotation.JSExport

import com.ikanow.aleph2.builder_ui.data_model._
import com.ikanow.aleph2.builder_ui.services._
import com.ikanow.aleph2.builder_ui.utils.ElementTreeBuilder

/**
 * Controller for the main page
 */
@JSExport
@injectable("bucketBuilderCtrl")
object BucketBuilderController extends Controller[Scope] {

  import js.JSConverters._

  val templateUrl = "templates/bucket_viewer.html"

  @inject
  var scope: ControllerData = _  
  
  @inject
  var modal: ModalService = _
  
  @inject
  var element_template_service: ElementTemplateService = _

  @inject
  var element_service: ElementService = _
    
  override def initialize(): Unit = {
    super.initialize()

    scope.breadcrumb = js.Array("Bucket")
    scope.breadcrumb_system = js.Array("Bucket")
    
    element_service.getMutableRoot().foreach { root => scope.curr_element = root }
    
    scope.element_grid = js.Array(
        ElementCardJs.buildDummy("Add content from 'Templates' list")
        )
        
    scope.element_grid_options = GridsterOptionsJs()
    
    scope.element_template_tree = js.Array()
   
    scope.element_template_tree_expanded = js.Array()
    
    scope.element_template_tree_opts = js.Dynamic.literal(
        dirSelectable = false
        )    
    
    recalculateTemplates()
  }

  def recalculateTemplates():Unit = {
    
    element_template_service.requestElementTemplates(true).foreach { beans => 
      {
          scope.element_template_array = beans.toArray
        
          scope.element_template_tree.clear()
          scope.element_template_tree.appendAll(
            ElementTreeBuilder.getTemplateTree(scope.breadcrumb_system, beans)
          )
          
          scope.element_template_tree_expanded.clear()
          scope.element_template_tree_expanded.appendAll(
            scope.element_template_tree.filter { node => node.category }.toJSArray
          )
          
          // (needed because of the future - not sure if this will still be the case if the promise is 
          //  composed from the http service - you can't nest $apply so will need to monitor this)
          scope.$apply("")
      }
    }    
  }
  
  @JSExport
  def insertElement(template: ElementTemplateNodeJs):Unit = {
    
    // Remove any dummy elements:
    scope.element_grid = scope.element_grid.filter { node => node.deletable }
    
    // Get the value
    val bean = scope.element_template_array(template.templateIndex)
    
    // Get current highest row:
    val max_row = 1 + scope.element_grid.map { card => card.row }.reduceOption(_ max _).getOrElse(-1)

    // Add new card
    val new_card = ElementCardJs(bean.display_name, max_row, 0, bean.expandable, bean)
    scope.element_grid.push(new_card)

    // Add to the current element's children
    scope.curr_element.children.push(
        ElementNodeJs(new_card.label, new_card, scope.curr_element)
        )
  }

  @JSExport
  def expandElement(item: ElementCardJs): Unit = {
    
    scope.curr_element.children.find { node => node.element == item }.foreach { new_node => {
    
      scope.curr_element = new_node
      
      scope.element_grid.clear()
      scope.element_grid.appendAll(scope.curr_element.children.map { node => node.element })
      
      // Update the breadcrumbs and get the next set of templates
      val new_path_el = item.template_json.get("key").get.toString()
      scope.breadcrumb.push(item.label)
      scope.breadcrumb_system.push(new_path_el)
      
      recalculateTemplates()
    }}
  }
  
  @JSExport
  def openElementConfig(item: ElementCardJs, size: String): Unit = {
      // Can't get resolve working so going via the service:
     element_service.setElementToEdit(item);
    
		  modal.open(
				  js.Dynamic.literal(
						  templateUrl = "templates/form_builder.html",
						  controller = "formBuilderCtrl", 
						  size = size
						  )
						  .asInstanceOf[ModalOptions]
				  )
  }

  @JSExport
  def openElementNavigator(size: String): Unit = {

		  modal.open(
				  js.Dynamic.literal(
						  templateUrl = "templates/quick_navigate.html",
						  controller = "quickNavigateCtrl", 
						  size = size
						  )
						  .asInstanceOf[ModalOptions] 
				  )
  }

  /**
   * The specific scope data used in this controller
   */
  @js.native
  trait ControllerData extends Scope {
    
    // Data Model
    
    var breadcrumb: js.Array[String] = js.native
    var breadcrumb_system: js.Array[String] = js.native

    var curr_element: ElementNodeJs = js.native
    
    var element_template_tree: js.Array[ElementTemplateNodeJs] = js.native
    var element_template_tree_expanded: js.Array[ElementTemplateNodeJs] = js.native
    var element_template_tree_opts: js.Object = js.native
    // Not visible by JS:
    var element_template_array: Array[ElementTemplateBean]
    
    var element_grid: js.Array[ElementCardJs] = js.native
    var element_grid_options: GridsterOptionsJs = js.native

  }
}

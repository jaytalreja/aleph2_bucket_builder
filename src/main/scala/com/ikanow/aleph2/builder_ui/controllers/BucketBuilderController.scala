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

import scala.collection.mutable._

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
    
  @inject
  var undo_redo_service: UndoRedoService = _
  
  @inject
  var json_gen_service: JsonGenerationService = _
  
  override def initialize(): Unit = {
    super.initialize()

    scope.breadcrumb = js.Array()
    scope.breadcrumb_system = js.Array()
    scope.element_grid = js.Array()        
    
    element_service.getMutableRoot().foreach { root => {
        scope.curr_element = root 
        rebuildBreadcrumbs(root)            
        
        buildGrid()
        
        if (scope.element_grid.isEmpty) {
          scope.element_grid.append(ElementCardJs.buildDummy("Add content from 'Templates' list"))
          //(don't watch this)
        }        
      }}
    
    scope.element_grid_options = GridsterOptionsJs(gridElementMoveOrResize_start _, gridElementMoveOrResize_end _)
        
    scope.element_template_tree = js.Array()
   
    scope.element_template_tree_expanded = js.Array()
    
    scope.element_template_tree_opts = js.Dynamic.literal(
        dirSelectable = false
        )    
    
    recalculateTemplates()
    
    scope.$on("quick_navigate", (event: Event, message: ElementNodeJs) => {
      navigateTo(message)
    })
    scope.$on("quick_navigate_and_open", (event: Event, message: ElementNodeJs) => {
      navigateTo(message.$parent)
      openElementConfig(message.element, "xl")
    })
  }

  var grid_mod_starting_topology: List[Tuple4[Int, Int, Int, Int]] = List()
  
  @JSExport
  def gridElementMoveOrResize_start(): Unit = {
    grid_mod_starting_topology = scope.element_grid.map { card => (card.row, card.col, card.sizeX, card.sizeY) }.toList
  }
  
  @JSExport
  def gridElementMoveOrResize_end(): Unit = {
    
    // Has it actually moved or resized?
    val new_topology = scope.element_grid.map { card => (card.row, card.col, card.sizeX, card.sizeY) }.toList
    if (!(grid_mod_starting_topology.equals(new_topology)))
    {    
      undo_redo_service.registerState(MoveOrResizeElements(scope.curr_element, grid_mod_starting_topology, new_topology))
      element_service.getMutableRoot().foreach { root => json_gen_service.generateJson(root) }    
    }
  }    
  
  @JSExport
  def undo(): Unit = {
    val maybe_change = undo_redo_service.restorePrevState(scope.curr_element)
    handleUndoRedo(maybe_change)
  }

  @JSExport
  def redo(): Unit = {    
    val maybe_change = undo_redo_service.redoUndoneState(scope.curr_element)
    handleUndoRedo(maybe_change)
  }

  protected def handleUndoRedo(maybe_change:Option[UndoRedoElement]):Unit = {
    maybe_change.foreach { change => change match {
      case AddElement(added) => {
        navigateTo(added.$parent)
      }
      case DeleteElement(deleted) => {
        navigateTo(deleted.$parent)
      }
      case ModifyElement(original, modded) => {
        navigateTo(original.$parent)
        this.openElementConfig(original.element, "xl")
      }
      case MoveOrResizeElements(original, _, _) => {
        navigateTo(original)
      }
    }}    
    element_service.getMutableRoot().foreach { root => json_gen_service.generateJson(root) }    
  }
  
  @JSExport
  def recalculateTemplates(reload: Boolean = false):Unit = {
    
    element_template_service.requestElementTemplates(!reload).foreach { beans => 
      {
          scope.element_template_array = beans
        
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
    val max_row = 1 + scope.element_grid.map { card => card.row + card.sizeY - 1 }.reduceOption(_ max _).getOrElse(-1)

    // Create new card
    val new_card = ElementCardJs(bean.display_name, max_row, 0, bean.expandable, bean)

    // Add to the current element's children
    val new_node = ElementNodeJs(new_card.label, new_card, scope.curr_element)
    scope.curr_element.children.push(new_node)
        
    // Rebuild grid (also resets all the watches)
    buildGrid()
    
    // Register with undo
    undo_redo_service.registerState(AddElement(new_node))
    
    // Recalculate derived json
    element_service.getMutableRoot().foreach { root => json_gen_service.generateJson(root) }   
    
    // Add watch
  }

  @JSExport
  def expandElementConfig(item: ElementCardJs): Unit = {
    
    scope.curr_element.children.find { node => node.element == item }.foreach { new_node => {
      navigateTo(new_node)
    }}
  }
  
 	// @param delta - if called externally must be <0 to go back, >0 to set to an abs value, 0 is for internal only
  @JSExport
  def navigateBack(delta: Int):Unit = {
    if ((delta < 0) && !scope.curr_element.root) {
      scope.curr_element = scope.curr_element.$parent
      if (-1 == delta)
        navigateTo(scope.curr_element)
      else
        navigateBack(delta + 1)
    }
    else if (delta > 0) {
      //eg 0 == Bucket, 1 == Bucket > blahA, 2 == Bucket > blahB
      navigateBack(delta - scope.breadcrumb.length)
    }
  }

  /** Rebuilds the grid based on the current element
   *  Used slightly inefficiently but keeps "all" the logic in one place
	 *  (originally had lots of complexities related to watching but those have now been removed, I use the gridster callbacks instead)
 	*/
  def buildGrid(): Unit = {
      scope.element_grid.clear()
      scope.element_grid.appendAll(scope.curr_element.children.map { node => node.element })
  }
  
  def navigateTo(new_node: ElementNodeJs): Unit = {
      scope.curr_element = new_node
      
      buildGrid()
      
      // Update the breadcrumbs and get the next set of templates
      rebuildBreadcrumbs(new_node)

      element_service.setElementLevel(new_node)
      
      recalculateTemplates()
  }
  
  def rebuildBreadcrumbs(new_node: ElementNodeJs):Unit = {
      scope.breadcrumb.clear()
      scope.breadcrumb.appendAll(
          rebuildBreadcrumb(List(), new_node, n => n.element.label).reverse
          )
      scope.breadcrumb_system.clear()
      scope.breadcrumb_system.appendAll(
          rebuildBreadcrumb(List(), new_node, n => n.element.template.key).reverse
          )    
  }
  
  def rebuildBreadcrumb(acc:List[String], element: ElementNodeJs, extractor: ElementNodeJs => String):List[String] = {
    if (element.root)
      element.label :: acc
    else 
      extractor(element) :: rebuildBreadcrumb(acc, element.$parent, extractor)
  }
  
  @JSExport
  def openElementConfig(item: ElementCardJs, size: String): Unit = {
      // Can't get resolve working so going via the service:
    scope.curr_element.children.find(node => node.element == item).foreach { node => {
       element_service.setElementToEdit(node);
      
  		  modal.open(
  				  js.Dynamic.literal(
  						  templateUrl = "templates/form_builder.html",
  						  controller = "formBuilderCtrl", 
  						  size = size
  						  )
  						  .asInstanceOf[ModalOptions]
  				  )
    }}
  }

  @JSExport
  def deleteElementConfig(card: ElementCardJs):Unit = {
    // Remove from list (bail if an internal error occurs)
    
    scope.curr_element.children.find(node => node.element == card).foreach { node => {
      scope.curr_element.children.remove(scope.curr_element.children.indexOf(node)) 
      
      // Rebuild grid (also resets all the watches)
      buildGrid()
      
      // Register with undo
      undo_redo_service.registerState(DeleteElement(node))      
      
      // Recalculate derived json
      element_service.getMutableRoot().foreach { root => json_gen_service.generateJson(root) }              
    }}
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

  @JSExport
  def openStatusViewer(size: String): Unit = {

		  modal.open(
				  js.Dynamic.literal(
						  templateUrl = "templates/status_viewer.html",
						  controller = "statusViewerCtrl", 
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

    var element_template_array: js.Array[ElementTemplateJs]
    
    var element_grid: js.Array[ElementCardJs] = js.native
    var element_grid_options: GridsterOptionsJs = js.native

  }
}

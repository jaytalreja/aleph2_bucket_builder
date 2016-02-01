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
import com.ikanow.aleph2.builder_ui.utils.JsOption

import scala.collection.mutable._

/**
 * Controller for the main page
 */
@JSExport
@injectable("bucketBuilderCtrl")
class BucketBuilderController(
    scope: BucketBuilderScope,
    modal: ModalService,
    element_service: ElementService,
    element_template_service: ElementTemplateService,
    undo_redo_service: UndoRedoService,
    json_gen_service: JsonGenerationService,
    global_io_service: GlobalInputOutputService
    )
    
  extends AbstractController[Scope](scope) {

  import js.JSConverters._

  override def initialize(): Unit = {
    super.initialize()

    // Basic initialization
    
    scope.breadcrumb = js.Array()
    scope.breadcrumb_system = js.Array()

    scope.element_grid = js.Array()        
    scope.has_errors = false            

    scope.element_grid_options = GridsterOptionsJs(gridElementMoveOrResize_start _, gridElementMoveOrResize_end _)
        
    scope.element_template_tree = js.Array()
   
    scope.element_template_tree_expanded = js.Array()
    
    scope.element_template_tree_opts = js.Dynamic.literal(
        dirSelectable = false
        )    
    
    // Async code called immediately:
        
    element_service.getMutableRoot().flatMap { root => {
      // Get breadcrumbs
      scope.curr_element = root 
      rebuildBreadcrumbs(root)
      scope.formception_mode = scope.breadcrumb(0) == "Template"
      
      // Get the templates (requires the breadcrumbs to filter)
      recalculateTemplates(root, scope.breadcrumb_system).map { unit => root }      
    }}
    .foreach { root => { // Now build the grid since we have the templates (needed the global JS from there, see below)
        buildGrid()
        
        if (scope.element_grid.isEmpty) {
          scope.element_grid.append(ElementCardJs.buildDummy("Add content from 'Templates' list"))
        }
        else { // build the generated object immediately
          
          // (Note this requires globals to have been registered hence after recalculateTemplates)
          //  TODO: make this more stable to eg the templates changing and removing a global used in the builder)
          regenerateJson()          
        }
        
        // Refresh:
        scope.$apply("");  
          
        // Finally, if there's a starting position set navigate there
    
        initialNavigation(root)        
    }}

    // Register some callbacks
    
    scope.$on("import_json", (event: Event, import_json: ElementNodeJs) => {
      element_service.getMutableRoot().foreach { root => {
        undo_redo_service.clearAll()
        
        root.children.clear();
        root.children.appendAll(import_json.children)
        ElementTreeBuilder.fillInImportedTree(root)
        
        scope.curr_element = root
        rebuildBreadcrumbs(root)
        buildGrid()
        regenerateJson()
        
        refreshTemplates()        
        
        initialNavigation(import_json)
      }}
    })
    scope.$on("quick_navigate", (event: Event, message: ElementNodeJs) => {
      navigateTo(message)
    })
    scope.$on("quick_navigate_and_open", (event: Event, message: ElementNodeJs) => {
      navigateTo(message.$parent)
      openElementConfig(message.element, "xl")
    })
  }

  var grid_mod_starting_topology: List[Tuple4[Int, Int, Int, Int]] = List()

  protected def initialNavigation(root: ElementNodeJs): Unit = {
      def getStartingElement(curr: ElementNodeJs, map: List[Int]): ElementNodeJs = {
        map match {
          case Nil => curr
          case head :: tail => getStartingElement(curr.children(head), tail)
        }
      }      
      JsOption(root.start_pos)
        .filter { array => !array.isEmpty }
        .map { array => getStartingElement(root, array.toList) }
        .foreach { x => navigateTo(x) }    
  }
  
  @JSExport
  def toggleSelect(): Unit = {
    scope.element_grid
          .sortBy { card => (card.row, card.col) }
          .headOption
          .foreach { card => {
            val new_enabled = !card.enabled
            scope.element_grid.foreach { other_card => other_card.enabled = new_enabled }
          }}
  }
  
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
      regenerateJson()
    }
  }    
  
  @JSExport
  def renderForm(): Unit = {
    // Special formception mode, render the form being built
    element_service.getMutableRoot().foreach { root => {

      val result_json = JSON.parse(global_io_service.generated_output_str()).asInstanceOf[js.Array[ElementTemplateJs]]

      def getParent(curr: ElementNodeJs): ElementNodeJs = {
        if (curr.root) curr
        else if (curr.$parent.root) curr
        else getParent(curr.$parent)
      }
      val top_of_tree = getParent(scope.curr_element)
      
      // Top level, nothing to render
      if (top_of_tree.root) return
      
      // Where am I in the grid?
      
      val index = 
      top_of_tree.$parent
        .children
        .sortBy { node => (node.element.row, node.element.col) }
        .indexOf(top_of_tree)
      
      val dummy_card = ElementCardJs(0, 0, false, result_json(index))
      val dummy_element = ElementNodeJs("", dummy_card, root)
      
  		  modal.open(
  				  js.Dynamic.literal(
  						  templateUrl = "templates/form_builder.html",
  						  controller = "formBuilderCtrl", 
  						  size = "xl",
  						  resolve = js.Dynamic.literal(
  						      node_to_edit = () => dummy_element,
  						      formception_mode = () => true
  						      )
  						      .asInstanceOf[js.Dictionary[js.Any]]
  						  )
  						  .asInstanceOf[ModalOptions]
  				  )
  				  .result.then((x: Unit) => {
  				    // (do nothing this is display only)
  				  })    				  
    }}
  }
  
  @JSExport
  def undo(): Unit = {
    val maybe_change = undo_redo_service.restorePrevState(scope.curr_element)
    handleUndoRedo(maybe_change)
  }

  @JSExport
  def hasUndo(): Boolean = undo_redo_service.numUndoElements() > 0 

  @JSExport
  def hasRedo(): Boolean = undo_redo_service.numRedoElements() > 0 
  
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
      case EnableOrDisableElement(node, _, _) => navigateTo(node.$parent)
      case ModifyElement(original, modded) => {
        navigateTo(original.$parent)
        this.openElementConfig(original.element, "xl")
      }
      case MoveOrResizeElements(original, _, _) => {
        navigateTo(original)
      }
    }}   
    regenerateJson()
  }
  
  @JSExport
  def refreshTemplates(reload: Boolean = false):Future[Unit] = {
    recalculateTemplates(scope.curr_element, scope.breadcrumb_system, reload)
  }
  
  def recalculateTemplates(root: ElementNodeJs, breadcrumb_system: js.Array[String], reload: Boolean = false):Future[Unit] = {
    
    val future = element_template_service.requestElementTemplates(!reload)
    
    future.foreach { beans => 
      {
          scope.element_template_array = beans
        
          scope.element_template_tree.clear()
          scope.element_template_tree.appendAll(
            ElementTreeBuilder.getTemplateTree(breadcrumb_system, root, beans)
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
    future.map { x => Unit }
  }
  
  @JSExport
  def duplicateElement(item: ElementCardJs): Unit = {
    scope.curr_element.children.find { node => node.element == item }.foreach { to_dup => {

      val new_node = JSON.parse(ElementTreeBuilder.stringifyTree(to_dup)).asInstanceOf[ElementNodeJs]

      // Fill in parents
      ElementTreeBuilder.fillInImportedTree(new_node)
      new_node.$parent = scope.curr_element
      
      val col_row = getNextGridPosition()
      new_node.element.col = col_row._1
      new_node.element.row = col_row._2
      
      insertNewNode_internal(new_node)
    }}
  }
  
  @JSExport
  def insertElement(template: ElementTemplateNodeJs):Unit = {
    
    // Remove any dummy elements:
    scope.element_grid = scope.element_grid.filter { node => node.deletable }
    
    // Get the value
    val bean = scope.element_template_array(template.templateIndex)
    
    val col_row = getNextGridPosition()
    
    // Create new card
    val new_card = ElementCardJs(col_row._2, col_row._1, bean.expandable, bean)

    // Add to the current element's children
    val new_node = ElementNodeJs(new_card.short_name, new_card, scope.curr_element)
    
    insertNewNode_internal(new_node)
  }

  protected def getNextGridPosition(): Tuple2[Int, Int] = {
    // Get current highest row:
    val tmp_max_row = scope.element_grid.map { card => card.row + card.sizeY - 1 }.reduceOption(_ max _).getOrElse(0)
    // Get current highest col:
    val tmp_max_col = scope.element_grid.filter { card => card.row == tmp_max_row }.map { card => card.col + card.sizeX - 1 }.reduceOption(_ max _).getOrElse(-1)

    val col_row = (tmp_max_col, tmp_max_row) match {
      case (c, r) if (c > 2) => (0, 1 + r)
      case (c, r) => (c + 1, r)
    }    
    col_row
  }
  
  protected def insertNewNode_internal(new_node: ElementNodeJs): Unit = {
    scope.curr_element.children.push(new_node)
        
    // Rebuild grid (also resets all the watches)
    buildGrid()
    
    // Register with undo
    undo_redo_service.registerState(AddElement(new_node))
    
    // Recalculate derived json
    regenerateJson()    
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
      
      recalculateTemplates(new_node, scope.breadcrumb_system)
  }
  
  def rebuildBreadcrumbs(new_node: ElementNodeJs):Unit = {
      scope.breadcrumb.clear()
      scope.breadcrumb.appendAll(
          rebuildBreadcrumb(List(), new_node, n => Option(n.label), n => n.element.short_name).reverse
          )
      scope.breadcrumb_system.clear()
      scope.breadcrumb_system.appendAll(
          rebuildBreadcrumb(List(), new_node, n => Option(n.label), n => n.element.template.key).reverse
          )    
          
      global_io_service.setStartingPosition(
          rebuildBreadcrumb(List(), new_node, n => Option.empty, n => n.$parent.children.indexOf(n)).reverse.toJSArray
          )
  }
  
  def rebuildBreadcrumb[A](acc:List[A], element: ElementNodeJs, on_root: ElementNodeJs => Option[A], extractor: ElementNodeJs => A):List[A] = {
    if (element.root)
      on_root(element).map { n => n :: acc }.getOrElse(acc)
    else 
      extractor(element) :: rebuildBreadcrumb(acc, element.$parent, on_root, extractor)
  }
  
  @JSExport
  def openElementConfig(item: ElementCardJs, size: String): Unit = {
    scope.curr_element.children.find(node => node.element == item).foreach { node => {
  		  modal.open(
  				  js.Dynamic.literal(
  						  templateUrl = "templates/form_builder.html",
  						  controller = "formBuilderCtrl", 
  						  backdrop = "static",
  						  size = size,
  						  resolve = js.Dynamic.literal(
  						      node_to_edit = () => node
  						      )
  						      .asInstanceOf[js.Dictionary[js.Any]]
  						  )
  						  .asInstanceOf[ModalOptions]
  				  )
  				  .result.then((x: Unit) => {
  				    // On modal success, regenerate the JSON
  				    regenerateJson()
  				  })
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
      regenerateJson()
    }}
  }
  
  protected def regenerateJson(): Unit = {
      element_service.getMutableRoot().foreach { root => {
        json_gen_service.generateJson(root)                   
        scope.has_errors = !json_gen_service.getCurrentErrors().isEmpty
      }}
  }
  
  @JSExport
  def enableOrDisableElement(card: ElementCardJs): Unit = {
    card.enabled = !card.enabled
    scope.curr_element.children.find(node => node.element == card).foreach { node => {
      undo_redo_service.registerState(EnableOrDisableElement(node, !card.enabled, card.enabled))
    }}
    regenerateJson()
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
}

object BucketBuilderController {
  val templateUrl = "templates/bucket_viewer.html"
}

/**
 * The specific scope data used in this controller
 */
@js.native
trait BucketBuilderScope extends Scope {
  
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
  
  var has_errors: Boolean = js.native

  var formception_mode: Boolean = js.native
}

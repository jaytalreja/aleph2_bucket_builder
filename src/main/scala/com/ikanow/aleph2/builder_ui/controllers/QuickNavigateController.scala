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
import scala.scalajs.js.JSApp
import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.extensions._

import ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.annotation.JSExport

import com.ikanow.aleph2.builder_ui.data_model._
import com.ikanow.aleph2.builder_ui.services._

/**
 * Controller for the main page
 */
@JSExport
@injectable("quickNavigateCtrl")
class QuickNavigateController(
    scope: QuickNavigationScope, 
    root_scope: RootScope, 
    modal: ModalInstance[Unit],
    element_service: ElementService
    ) extends AbstractController[Scope](scope) {

  import js.JSConverters._

  override def initialize(): Unit = {
    super.initialize()
    
    scope.selected_item = element_service.getElementLevel()

    element_service.getMutableRoot().foreach { root => { 
      scope.element_tree = js.Array(root)
      //expand all:
      scope.element_tree_expanded = flatten(List(), root).toJSArray
    }}
    
    scope.element_tree_opts = js.Dynamic.literal(
        //(no options)
        )    
    
  }

  def flatten(acc: List[ElementNodeJs], root: ElementNodeJs): List[ElementNodeJs] = {
    if (null == root.children)
      root :: acc
    else
      root :: acc ++ root.children.flatMap { child => flatten(acc, child) }
  }
  
  @JSExport
  def displayLine(node: ElementNodeJs) = {
    if (node.root)
      node.label
    else 
      node.element.short_name
  }
  
  @JSExport
  def ok(): Unit = {    
    
    val to_navigate:ElementNodeJs = 
        if ((scope.selected_item.root) || (scope.selected_item.element.expandable)) scope.selected_item
        else scope.selected_item.$parent
    
    root_scope.$broadcast("quick_navigate", to_navigate)
    
    modal.close()
  }  
  
  @JSExport
  def cancel(): Unit = {
    modal.close()
  }
  
  @JSExport
  def isSelectable(node: ElementNodeJs):Boolean = node.element.expandable
}

/**
 * The specific scope data used in this controller
 */
@js.native
trait QuickNavigationScope extends Scope {
  var selected_item: ElementNodeJs = js.native
  
  var element_tree: js.Array[ElementNodeJs] = js.native
  var element_tree_expanded: js.Array[ElementNodeJs] = js.native
  
  var element_tree_opts: js.Object = js.native
}


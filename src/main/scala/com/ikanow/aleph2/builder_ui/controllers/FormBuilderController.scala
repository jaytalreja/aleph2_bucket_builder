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
import scala.scalajs.js.JSON

import js.JSConverters._

import scalatags.Text.all._

import com.ikanow.aleph2.builder_ui.data_model._
import com.ikanow.aleph2.builder_ui.services._

/**
 * Controller for the main page
 */
@JSExport
@injectable("formBuilderCtrl")
class FormBuilderController(
    scope: FormBuilderScope,
    rootScope: RootScope,
    element_service: ElementService,
    undo_redo_service: UndoRedoService,
    modal: ModalInstance[Unit])
    
  extends AbstractController[Scope](scope) {

  override def initialize(): Unit = {
    super.initialize()

    val curr_card_node = element_service.getElementToEdit();
    
    fields = curr_card_node.element.form_metadata
    
    // deep copy:
    model = JSON.parse(JSON.stringify(curr_card_node.element.form_model)).asInstanceOf[js.Dictionary[js.Any]]
    
    scope.form_info_html = {
      if (curr_card_node.element.form_info.trim().startsWith("<"))
        curr_card_node.element.form_info
      else
        p(curr_card_node.element.form_info).toString()
    }
  }

  @JSExport
  var form_info_html: String = "<p></p>"
  
  @JSExport
  var model: js.Dictionary[js.Any] = null

  @JSExport
  var fields: js.Array[js.Any] = js.Array()
    
  @JSExport
  def ok(): Unit = {    
    val curr_card_node = element_service.getElementToEdit();
    
    // First register with undo service
    
    //TODO: not working
    //undo_redo_service.registerState(ModifyElement(curr_card_node, curr_card_node))
    
    // Now mutate the state
    
    curr_card_node.element.form_model.clear()
    model.map { case (key, value) => curr_card_node.element.form_model.put(key, value) }    
    
    modal.close()
  }  
  
  @JSExport
  def cancel(): Unit = {
    modal.close()
  }    
}

/**
 * The specific scope data used in this controller
 */
@js.native
trait FormBuilderScope extends Scope {
  var form_info_html: String = js.native
}

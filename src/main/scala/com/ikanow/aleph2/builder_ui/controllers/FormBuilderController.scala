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
    root_scope: RootScope,
    element_service: ElementService,
    undo_redo_service: UndoRedoService,
    json_gen_service: JsonGenerationService,
    modal: ModalInstance[Unit])
    
  extends AbstractController[Scope](scope) {

  val short_name_schema = """
  {
		"key": "_short_name",
		"type": "input",
		"templateOptions": {
			"type": "text",
			"label": "Short Name",
			"placeholder": "A Short Name For This Element",
			"required": true
		}
		}
		"""

  val summary_schema = """
  {
		"key": "_summary",
		"type": "input",
		"templateOptions": {
			"type": "text",
			"label": "Summary",
			"placeholder": "A Short Summary Of This Element's Function",
			"required": false
		}
		}
		"""
  
  override def initialize(): Unit = {
    super.initialize()

    val curr_card_node = element_service.getElementToEdit();
    
    scope.element_expands = curr_card_node.element.expandable
    
    scope.form_template_name = curr_card_node.element.template.display_name
    
    scope.element_errors = json_gen_service.getCurrentErrors().filter { case (err, el) => el == curr_card_node }. map { case (err, el) => err }.toJSArray
    scope.element_has_errors = !scope.element_errors.isEmpty
    
   fields.clear()
   fields.appendAll(
       Option(curr_card_node.element.template.schema)
         .map { x => x.asInstanceOf[js.Array[js.Any]] }
         .getOrElse(js.Array())
         .map { element => JSON.parse(JSON.stringify(element)) } // (deep copy)
         .toList
        )

    JSON.parse(short_name_schema) +=: JSON.parse(summary_schema) +=: fields

    // deep copy:
    model = JSON.parse(JSON.stringify(curr_card_node.element.form_model)).asInstanceOf[js.Dictionary[js.Any]]
    model.put("_short_name",  curr_card_node.element.short_name)
    model.put("_summary",  curr_card_node.element.summary)    
    
    scope.form_info_html = {
      if (curr_card_node.element.template.form_info.trim().startsWith("<"))
        curr_card_node.element.template.form_info
      else
        p(curr_card_node.element.template.form_info).toString()
    }
  }

  @JSExport
  def expandElementConfig(): Unit = {
    if (!scope.element_expands) return
    
    root_scope.$broadcast("quick_navigate", element_service.getElementToEdit())
    ok()    
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
    
    undo_redo_service.registerState(ModifyElement(curr_card_node, curr_card_node))
    
    // Now mutate the state
    
    curr_card_node.element.form_model.clear()
    model.map { case (key, value) =>
      if (key.equals("_short_name"))
          curr_card_node.element.short_name = value.toString()
      else if (key.equals("_summary"))
          curr_card_node.element.summary = value.toString()
      else
        curr_card_node.element.form_model.put(key, value) 
    }
    
    element_service.getMutableRoot().foreach { root => json_gen_service.generateJson(root) }    
    
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
  
  var form_template_name: String = js.native
  
  var element_has_errors: Boolean = js.native
  
  var element_errors: js.Array[String] = js.native
  
  var element_expands: Boolean = js.native
}

/** Configures the formly element
 * @author alex
 */
@JSExport
@injectable("formlyConfig")
object FormlyConfig extends Config { 
  @inject
  var formlyConfigProvider: FormlyConfigProvider = _

  override def initialize() {
    
    formlyConfigProvider.setType(js.Dynamic.literal(
          name = "code_input",
          template = 
            """
            <textarea rows="32" ui-codemirror="options.templateOptions" ng-model="model[options.key]"></textarea>
            """
        )
        .asInstanceOf[js.Dictionary[js.Any]]
     )
  }  
}

@js.native
@injectable("formlyConfigProvider")
/** See http://docs.angular-formly.com/docs/custom-templates
 * @author alex
 */
trait FormlyConfigProvider extends js.Object {
  def setType(options: js.Dictionary[js.Any]): this.type = js.native
}


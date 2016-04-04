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
import com.ikanow.aleph2.builder_ui.utils.JsOption

/**
 * Controller for the main page
 */
@JSExport
@injectable("formBuilderCtrl")
class FormBuilderController(
    scope: FormBuilderScope,
    root_scope: RootScope,
    element_service: ElementService,
    element_template_service: ElementTemplateService,
    undo_redo_service: UndoRedoService,
    json_gen_service: JsonGenerationService,
    modal: ModalInstance[Unit],
    node_to_edit: ElementNodeJs
    )
    
  extends AbstractController[Scope](scope) {

  val short_name_schema = """
  {
		"key": "_short_name",
		"type": "horizontalInput",
		"templateOptions": {
			"label": "Short Name",
			"placeholder": "A Short Name For This Element",
			"required": true
		}
		}
		"""

  val summary_schema = """
  {
		"key": "_summary",
		"type": "horizontalTextArea",
		"templateOptions": {
			"label": "Summary",
			"placeholder": "A Short Summary Of This Element's Function",
			"required": false
		}
		}
		"""
  
  val line_separator = """
    {
      "template": "<hr/>"
    }
  """
  
  override def initialize(): Unit = {
    super.initialize()

    val curr_card_node = node_to_edit
    
    scope.element_expands = curr_card_node.element.expandable
    
    scope.form_template_name = curr_card_node.element.template.display_name
    
    scope.element_errors = json_gen_service.getCurrentErrors().filter { case (err, el) => el == curr_card_node }. map { case (err, el) => err }.toJSArray
    scope.element_has_errors = !scope.element_errors.isEmpty

    // Pull out _short_name override
   val grouped_fields =  
       JsOption(curr_card_node.element.template.schema)
         .map { x => x.asInstanceOf[js.Array[js.Any]] }
         .getOrElse(js.Array())
         .map { element => JSON.parse(JSON.stringify(element)).asInstanceOf[js.Dictionary[js.Any]] } // (deep copy)
         .groupBy { x => x.get("key").filter { name => name.equals("_short_name") || name.equals("_summary") }.getOrElse("") }
    
   fields.clear()
   fields.appendAll(
       grouped_fields.getOrElse("", js.Array()).toList
        )

    val this_short_name_schema = grouped_fields.get("_short_name").filter { short_name => !short_name.isEmpty }.map { short_name => short_name.pop }.getOrElse(JSON.parse(short_name_schema))      
    val this_summary_schema = grouped_fields.get("_summary").filter { summary => !summary.isEmpty }.map { summary => summary.pop }.getOrElse(JSON.parse(summary_schema))      
    this_short_name_schema +=: this_summary_schema +=: JSON.parse(line_separator) +=: fields    
    
    // deep copy:
    model = JsOption(curr_card_node.element.form_model)
              .map { j => JSON.parse(JSON.stringify(j)) }
              .getOrElse(js.Dynamic.literal())
              .asInstanceOf[js.Dictionary[js.Any]]            
    
    model.put("_short_name",  curr_card_node.element.short_name)
    model.put("_summary",  JsOption(curr_card_node.element.summary).getOrElse("").asInstanceOf[String])    

    scope.form_info_html = {
      if (curr_card_node.element.template.form_info.trim().startsWith("<"))
        curr_card_node.element.template.form_info
      else
        p(curr_card_node.element.template.form_info).toString()
    }
              
     // If there's a unique template then allow overwrite:
     
     element_template_service.requestElementTemplates(true)
       .foreach { templates => { 
         val filtered_array_1 = templates
           .filter { template => template.display_name ==  curr_card_node.element.template.display_name }

         if (filtered_array_1.size == 1) { // (nasty if block)
           val filtered_array = 
                 filtered_array_1
                   .filter { template => JSON.stringify(template) != JSON.stringify(curr_card_node.element.template) } //(don't bother if they're the same)
           
           if (1 == filtered_array.size) {// (only if _exactly_ 1)
             scope.latest_template = filtered_array(0)
             scope.template_update_explanation = "System template is different to this. Press this button to update (WARNING: may cause the bucket to stop working)"
           }
           else {
             scope.template_update_explanation = "System template with the same name is identical."           
           }           
         }  
         else if (filtered_array_1.isEmpty) {
           scope.template_update_explanation = "No system template with this name."                      
         }
         else {
           scope.template_update_explanation = "Multiple system templates with this name found - couldn't resolve which one."           
         }
         
        // Refresh:
        scope.$apply("");  
       }}
  }

  @JSExport
  def updateTemplate(): Unit = {
    node_to_edit.element.template = scope.latest_template
    modal.close()
  }
  
  @JSExport
  def expandElementConfig(): Unit = {
    if (!scope.element_expands) return
    
    root_scope.$broadcast("quick_navigate", node_to_edit)
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
    val curr_card_node = node_to_edit;
    
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
    
    modal.close() // (this will trigger JSON regen in the form builder)
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
  
  var latest_template: ElementTemplateJs = js.native
  
  var template_update_explanation: String = js.native
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
    
    // 1) Very simple:
    
    formlyConfigProvider.setType(js.Dynamic.literal(
        name = "horizontalInput",
        template = """
            <div class="form-horizontal">
              <div class="form-group">
                <label class="control-label col-sm-3">{{to.required ? "* " : " "}}{{to.label}}</label>
                <div class="col-sm-9">
                  <input class="form-control" type="text" ng-model="model[options.key]"/>
                </div>
              </div>
          </div>
        """,
        wrapper = js.Array("bootstrapHasError")
        )
        .asInstanceOf[js.Dictionary[js.Any]]
     )

    formlyConfigProvider.setType(js.Dynamic.literal(
        name = "horizontalSelect",
        `extends` = "select",
        template = """
            <div class="form-horizontal">
              <div class="form-group">
                <label class="control-label col-sm-3">{{to.required ? "* " : " "}}{{to.label}}</label>
                <div class="col-sm-9">
                  <select class="form-control" ng-model="model[options.key]"></select>
                </div>
              </div>
          </div>
        """,
        wrapper = js.Array("bootstrapHasError")
        )
        .asInstanceOf[js.Dictionary[js.Any]]
     )

    formlyConfigProvider.setType(js.Dynamic.literal(
        name = "horizontalTextArea",
        template = """
            <div class="form-horizontal">
              <div class="form-group">
                <label class="control-label col-sm-3">{{to.required ? "* " : " "}}{{to.label}}</label>
                <div class="col-sm-9">
                  <textarea class="form-control" type="text" ng-model="model[options.key]"/>
                </div>
              </div>
          </div>
        """,
        wrapper = js.Array("bootstrapHasError")
        )
        .asInstanceOf[js.Dictionary[js.Any]]
     )

    formlyConfigProvider.setType(js.Dynamic.literal(
        name = "horizontalCheckbox",
        template = """
            <div class="form-horizontal">
              <div class="form-group">
                <div class="col-sm-offset-3 col-sm-9">
                  <div class="checkbox">
                    <label><input type="checkbox" class="formly-field-checkbox" ng-model="model[options.key]">{{to.label}} {{to.required ? '*' : ''}}</label>
                  </div>
                </div>
              </div>
          </div>
        """,
        wrapper = js.Array("bootstrapHasError")
        )
        .asInstanceOf[js.Dictionary[js.Any]]
     )
     
    // 2) Slightly more complex
    
    // Code mirror:
    // (currently simple but need to add logic)
    formlyConfigProvider.setType(js.Dynamic.literal(
          name = "code_input",
          template = 
            """
            <h4>{{options.templateOptions.label}}</h4>
            <textarea rows="32" ui-codemirror="options.templateOptions.codemirror" ng-model="model[options.key]"></textarea>
            """
        )
        .asInstanceOf[js.Dictionary[js.Any]]
     )
    
     // 3) Quite complex!
     
    // Simple Multi input: (taken from http://angular-formly.com/#/example/other/multi-input)
    formlyConfigProvider.setType(js.Dynamic.literal(
          name = "multiInput",
          templateUrl = "templates/form_builder_multiInput.html",
          defaultOptions = 
            js.Dynamic.literal(
              noFormControl = true,
              wrapper = js.Array("bootstrapLabel", "bootstrapHasError"),
              defaultValue = js.Array(),
              templateOptions = js.Dynamic.literal(
                  inputOptions = js.Dynamic.literal(
                      wrapper = null //(not sure what this is doing, just came from c/p)
                      )
                  )
              )
          ,
          controller = 
            js.eval("""my_controller = /* @ngInject */ function($scope) {
            $scope.copyItemOptions = copyItemOptions;
            function copyItemOptions(x) {
              return angular.copy(x);
            }            
          }
          """) // (currently can't get controller working as a scala method, get horrible $$scope/$$scopeProvider error)
        )
        .asInstanceOf[js.Dictionary[js.Any]]
     )
     
     // Complex multi input (taken from http://angular-formly.com/#/example/advanced/repeating-section)
     
    formlyConfigProvider.setType(js.Dynamic.literal(
          name = "repeatSection",
          templateUrl = "templates/form_builder_repeatSection.html",
          defaultOptions = 
            js.Dynamic.literal(
              defaultValue = js.Array(),
              templateOptions = js.Dynamic.literal(
                  addSectionText = "Add",
                  fields = js.Array()
                  )
              )
          ,
          controller = 
            js.eval("""var unique = 1; my_controller = function($scope) {
                   $scope.formOptions = {formState: $scope.formState};
                    $scope.addNew = addNew;                    
                    $scope.copyFields = copyFields;                    
                    
                    function copyFields(fields) {
                      fields = angular.copy(fields);
                      addRandomIds(fields);
                      return fields;
                    }
                    
                    function addNew() {
                      $scope.model[$scope.options.key] = $scope.model[$scope.options.key] || [];
                      var repeatsection = $scope.model[$scope.options.key];
                      var lastSection = repeatsection[repeatsection.length - 1];
                      var newsection = {};
                      repeatsection.push(newsection);
                    }
                    
                    function addRandomIds(fields) {
                      unique++;
                      angular.forEach(fields, function(field, index) {
                        if (field.fieldGroup) {
                          addRandomIds(field.fieldGroup);
                          return; // fieldGroups don't need an ID
                        }
                        
                        if (field.templateOptions && field.templateOptions.fields) {
                          addRandomIds(field.templateOptions.fields);
                        }
                        
                        field.id = field.id || (field.key + '_' + index + '_' + unique + getRandomInt(0, 9999));
                      });
                    }
                    
                    function getRandomInt(min, max) {
                      return Math.floor(Math.random() * (max - min)) + min;
                    }
                  }
          """) // (currently can't get controller working as a scala method, get horrible $$scope/$$scopeProvider error)
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
  def setWrapper(options: js.Dictionary[js.Any]): this.type = js.native
}


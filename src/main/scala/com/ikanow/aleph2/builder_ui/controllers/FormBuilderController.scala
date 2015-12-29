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

import com.ikanow.aleph2.builder_ui.data_model._

/**
 * Controller for the main page
 */
@JSExport
@injectable("formBuilderCtrl")
object FormBuilderController extends Controller[Scope] {

  import js.JSConverters._

  @inject
  var scope: ControllerData = _  
  
  @inject
  var modal: ModalInstance[Unit] = _
  
  override def initialize(): Unit = {
    super.initialize()

    //TODO: dummy data
    fields =
      List(
            FormConfigBean(
                key = "enabled",
                `type` = "checkbox",
                templateOptions = FormConfigTemplateBean(
                    label = "Enabled?"
                    )
                ),
            FormConfigBean(
                key = "input_directory",
                `type` = "input",
                templateOptions = FormConfigTemplateBean(
                    label = "Input Spool Directory",
                    `type` = "text",
                    placeholder = "Input path",
                    required = true
                    )
                ),
            FormConfigBean(
                key = "columns",
                `type` = "input",
                templateOptions = FormConfigTemplateBean(
                    label = "Columns",
                    `type` = "textarea",
                    placeholder = "Comma separated list of fields",
                    required = true
                    )
                )
    )
    .map { bean => JSON.parse(upickle.default.write(bean)).asInstanceOf[js.Any] }
    .toJSArray  
    
    //TODO: convert to HTML if doesn't start with \s*< (stick in a util somewhere)
    scope.form_info_html = "<p>TODO instructions here</p><p>From ElementTemplateBean</p>"
    
  }

  @JSExport
  var model: js.Object = js.Object()

  @JSExport
  var fields: js.Array[js.Any] = js.Array()
    
  @JSExport
  def ok(): Unit = {    
    modal.close()
  }  
  
  @JSExport
  def cancel(): Unit = {
    modal.close()
  }  
  
  /**
   * The specific scope data used in this controller
   */
  @js.native
  trait ControllerData extends Scope {
    var form_info_html: String = js.native
  }
}

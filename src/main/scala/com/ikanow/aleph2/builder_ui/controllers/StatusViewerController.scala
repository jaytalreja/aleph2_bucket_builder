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
@injectable("statusViewerCtrl")
class StatusViewerController(
    scope: StatusViewerScope, 
    modal: ModalInstance[Unit],
    global_io_service: GlobalInputOutputService
    ) extends AbstractController[Scope](scope) {

  import js.JSConverters._

  override def initialize(): Unit = {
    super.initialize()
    
    scope.errors = "No errors"
    
    scope.build_json = JSON.parse(global_io_service.config_output_str())
    
    scope.result_json = JSON.parse(global_io_service.generated_output_str())
   
    scope.buildOptions = js.Dynamic.literal(
        lineNumbers = true,
        readOnly = "nocursor",
        mode = "javascript"
        )
        .asInstanceOf[js.Object]
    
    scope.resultOptions = js.Dynamic.literal(
        lineNumbers = true,
        readOnly = "nocursor",
        mode = "javascript"
        )
        .asInstanceOf[js.Object]
    
    scope.errorOptions = js.Dynamic.literal(
        lineNumbers = true,
        readOnly = "nocursor"
        )
        .asInstanceOf[js.Object]
  }

  @JSExport
  def ok(): Unit = {    
    modal.close()
  }    
}

/**
 * The specific scope data used in this controller
 */
@js.native
trait StatusViewerScope extends Scope {

  @js.native
  var errors: String = js.native
  
  @js.native
  var build_json: js.Any = js.native

  @js.native
  var result_json: js.Any = js.native

  @js.native
  var errorOptions: js.Any = js.native
  
  @js.native
  var resultOptions: js.Any = js.native
  
  @js.native
  var buildOptions: js.Any = js.native
}


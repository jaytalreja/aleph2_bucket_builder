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
object QuickNavigateController extends Controller[Scope] {

  import js.JSConverters._

  @inject
  var scope: ControllerData = _  
  
  @inject
  var modal: ModalInstance[Unit] = _
  
  @inject
  var element_template_service: ElementTemplateService = _
  
  override def initialize(): Unit = {
    super.initialize()
    
    scope.element_tree = js.Array(
        ElementNode("test1"),
        ElementNode("test2",
            js.Array(
                ElementNode("test2_1"),
                ElementNode("test2_2")
                )
            )
        )
  }

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
    
    var element_tree: js.Array[ElementNode] = js.native
  }
}

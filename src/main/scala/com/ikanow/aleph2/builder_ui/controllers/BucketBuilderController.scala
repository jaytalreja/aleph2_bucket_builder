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

import ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.annotation.JSExport

import com.ikanow.aleph2.builder_ui.data_model._

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
  
  override def initialize(): Unit = {
    super.initialize()
    
    //setup eg scope.title etc
    
    //TODO: some dummy setups
    
    scope.breadcrumb = js.Array("Bucket")
    
    scope.element_template_tree = js.Array(
        ElementTemplateNode("test1"),
        ElementTemplateNode("test2",
            js.Array(
                ElementTemplateNode("test2_1"),
                ElementTemplateNode("test2_2")
                )
            )
        )
        
    scope.element_grid = js.Array(
        ElementCard(1, 1),
        ElementCard(3, 3)
        )
        
    scope.element_grid_options = GridsterOptions()
  }

  /**
   * The specific scope data used in this controller
   */
  @js.native
  trait ControllerData extends Scope {
    var breadcrumb: js.Array[String] = js.native
    
    var element_template_tree: js.Array[ElementTemplateNode] = js.native
    
    var element_grid: js.Array[ElementCard] = js.native
    var element_grid_options: GridsterOptions = js.native
  }
}

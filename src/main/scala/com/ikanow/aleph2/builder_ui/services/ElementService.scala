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
package com.ikanow.aleph2.builder_ui.services

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
import com.ikanow.aleph2.builder_ui.utils._

/** Retrieves the templates
 * @author alex
 */
@injectable("elementService")
class ElementService(global_io_service: GlobalInputOutputService) {
  
  val root:ElementNodeJs = global_io_service.config_input_object()
                            .map { start_obj => ElementTreeBuilder.fillInImportedTree(start_obj) }
                            .getOrElse(ElementNodeJs.buildRoot(global_io_service.root_element()))
      
  def getMutableRoot(): Future[ElementNodeJs] = {   
    Future.successful(root)
  }

  def setElementToEdit(new_card_node: ElementNodeJs):Unit = {
    curr_card_node = new_card_node;
  }
  def getElementToEdit():ElementNodeJs = curr_card_node
  
  var curr_card_node: ElementNodeJs = null;
  
  def setElementLevel(new_level: ElementNodeJs):Unit = {
    curr_level = new_level
  }
  
  def getElementLevel():ElementNodeJs = curr_level
  
  var curr_level: ElementNodeJs = root;
}

@injectable("elementService")
class ElementServiceFactory(global_io_service: GlobalInputOutputService) extends Factory[ElementService] {
  override def apply() = new ElementService(global_io_service)
}

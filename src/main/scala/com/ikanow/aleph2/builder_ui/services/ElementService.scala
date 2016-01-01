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

/** Retrieves the templates
 * @author alex
 */
@injectable("elementService")
class ElementService {
  
  val root:ElementNodeJs = ElementNodeJs.buildRoot()
  
  def getMutableRoot(): Future[ElementNodeJs] = {    
    Future.successful(root)
  }

  def setElementToEdit(new_card: ElementCardJs):Unit = {
    curr_card = new_card;
  }
  def getElementToEdit():ElementCardJs = curr_card
  
  var curr_card: ElementCardJs = null;
  
  def setElementLevel(new_level: ElementNodeJs):Unit = {
    curr_level = new_level
  }
  
  def getElementLevel():ElementNodeJs = curr_level
  
  var curr_level: ElementNodeJs = root;
}

@injectable("elementService")
class ElementServiceFactory extends Factory[ElementService] {
  override def apply() = new ElementService
}

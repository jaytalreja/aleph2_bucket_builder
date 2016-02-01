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
import com.ikanow.aleph2.builder_ui.utils.JsOption

/** Retrieves the templates
 * @author alex
 */
@injectable("globalIoService")
class GlobalInputOutputService {

  // INPUT VARIABLES
  
  def root_element(): String = js.Dynamic.global.aleph2_json_builder__root_element.asInstanceOf[String]
  
  def template_url(): Option[String] = JsOption(js.Dynamic.global.aleph2_json_builder__template_url).map { el => el.asInstanceOf[String] }

  def template_conversion_fn: js.Function1[js.Any, js.Array[js.Any]] = js.Dynamic.global.aleph2_json_builder__template_conversion_fn.asInstanceOf[js.Function1[js.Any, js.Array[js.Any]]]
  
  def config_input_object(): Option[ElementNodeJs] = 
    JsOption(js.Dynamic.global.aleph2_json_builder__config_input_obj).map { obj => obj.asInstanceOf[ElementNodeJs] }
    
  def generated_input_object(): Option[js.Dictionary[js.Any]] = 
    JsOption(js.Dynamic.global.aleph2_json_builder__generated_input_obj).map { obj => obj.asInstanceOf[js.Dictionary[js.Any]] }  

  def get_input_root_fn: Option[js.Function1[js.Dictionary[js.Any], js.Any]] = 
    JsOption(js.Dynamic.global.aleph2_json_builder__get_input_root_fn).map { obj => obj.asInstanceOf[js.Function1[js.Dictionary[js.Any], js.Any]] }
    
  // OUTPUT VARIABLES
  
  def config_output_str(): String = js.Dynamic.global.aleph2_json_builder__config_output_str.asInstanceOf[String]
  
  def generated_output_str(): String = js.Dynamic.global.aleph2_json_builder__generated_output_str.asInstanceOf[String]
  
  def errors(): js.Array[String] = js.Dynamic.global.aleph2_json_builder__output_errs.asInstanceOf[js.Array[String]]
  
  def starting_position(): js.Array[Int] = js.Dynamic.global.aleph2_json_builder__output_start_pos.asInstanceOf[js.Array[Int]]
  
  def setConfigOutputStr(str: String): Unit = {
    js.Dynamic.global.aleph2_json_builder__config_output_str = str
  }
  def setGeneratedOutputStr(str: String): Unit = {
    js.Dynamic.global.aleph2_json_builder__generated_output_str = str
  }
  def setErrors(errs: js.Array[String]) = {
    js.Dynamic.global.aleph2_json_builder__output_errs = js.Array().concat(errs)
  }
  def setStartingPosition(pos: js.Array[Int]) = {
    js.Dynamic.global.aleph2_json_builder__output_start_pos = js.Array().concat(pos)
  }
}

@injectable("globalIoService")
class GlobalInputOutputServiceFactory extends Factory[GlobalInputOutputService] {
  override def apply() = new GlobalInputOutputService
}

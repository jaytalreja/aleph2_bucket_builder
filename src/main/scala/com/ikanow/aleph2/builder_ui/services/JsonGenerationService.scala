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
import scala.scalajs.js.JSON
import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.extensions._

import ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.JSConverters._

import com.ikanow.aleph2.builder_ui.data_model._
import com.ikanow.aleph2.builder_ui.utils._
import scala.collection.mutable.MutableList

/** Creates the final outputs
 * @author alex
 */
@injectable("jsonGenService")
class JsonGenerationService(global_io_service: GlobalInputOutputService) {
  
  protected val curr_errors: MutableList[Tuple2[String, ElementNodeJs]] = MutableList()
  
  def getCurrentErrors(): List[Tuple2[String, ElementNodeJs]] = curr_errors.toList
  
  def generateJson(root: ElementNodeJs) = {
    
    // Output the config
    
    global_io_service.setConfigOutputStr(ElementTreeBuilder.stringifyTree(root))    
    
    val start_obj_root = JSON.parse(
        global_io_service.generated_input_object().map { obj => JSON.stringify(obj) }.getOrElse("{}")
        )
        .asInstanceOf[js.Dictionary[js.Any]] // (ie deep copy)

    val start_obj_curr =  global_io_service.get_input_root_fn.map { fn => fn(start_obj_root) }.getOrElse(start_obj_root)    
    
    //TODO (#6): for each card, also store the Formly validation errors for each card and then just always add those...
    // (then I think in the form itself, filter them out but display the "real-time" validation errors)
    
    curr_errors.clear()
    ElementTreeBuilder.generateOutput(root, root, start_obj_curr, start_obj_root, List(), List(), List(), curr_errors)
    
    global_io_service.setGeneratedOutputStr(JSON.stringify(start_obj_root))
    global_io_service.setErrors(curr_errors.map { t2 => t2._1 }.toJSArray)
  }
}

@injectable("jsonGenService")
class JsonGenerationServiceFactory(global_io_service: GlobalInputOutputService) extends Factory[JsonGenerationService] {
  override def apply() = new JsonGenerationService(global_io_service)
}

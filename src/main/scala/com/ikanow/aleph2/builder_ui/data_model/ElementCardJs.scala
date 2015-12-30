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
package com.ikanow.aleph2.builder_ui.data_model

import com.greencatsoft.angularjs.core._
import scala.scalajs.js
import com.greencatsoft.angularjs._
import scala.scalajs.js.JSON

@js.native
/** Represents a grid element
 * @author alex
 */
trait ElementCardJs extends js.Object {  
  // Display params
  val label: String = js.native
  val sizeX: Int = js.native
  val sizeY: Int = js.native
  val row: Int = js.native
  val col: Int = js.native  
  
  // Control
  val expandable: Boolean = js.native
  val configurable: Boolean = js.native
  val deletable: Boolean = js.native
  
  // Template info
  val form_metadata: js.Array[js.Any] = js.native
  val form_info: String = js.native
  val template_json:js.Object = js.native
}

object ElementCardJs {
  def apply(label: String, row: Int, col: Int, expandable: Boolean, template: ElementTemplateBean): ElementCardJs = {
    val form_info = template.form_info
    
    import upickle.default._
    
    val template_json = JSON.parse(write(template)).asInstanceOf[js.Dictionary[js.Any]]
    val form_metadata = template_json.get("schema").getOrElse(js.Dynamic.literal).asInstanceOf[js.Any]
    
    js.Dynamic.literal(label = label, row = row, col = col, sizeX = 1, sizeY = 1, 
                        expandable = expandable, configurable = true, deletable = true,
                        form_metadata = form_metadata, form_info = form_info, template_json = template_json
                        )
      .asInstanceOf[ElementCardJs]
  }
  def buildDummy(label: String):ElementCardJs =
    js.Dynamic.literal(label = label, row = 0, col = 0, sizeX = 1, sizeY = 1, 
                        expandable = false, configurable = false, deletable = false)
      .asInstanceOf[ElementCardJs]
    
}



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
import com.ikanow.aleph2.builder_ui.utils.JsOption

@js.native
/** Represents a grid element
 * @author alex
 */
trait ElementCardJs extends js.Object {  
  // Display params
  var enabled: Boolean = js.native 
  var sizeX: Int = js.native
  var sizeY: Int = js.native
  var row: Int = js.native
  var col: Int = js.native  
  
  // Control
  val expandable: Boolean = js.native
  val configurable: Boolean = js.native
  val deletable: Boolean = js.native
  
  // Template info
  var short_name: String = js.native
  var summary: js.UndefOr[String] = js.native
  var template: ElementTemplateJs = js.native
  val form_model: js.Dictionary[js.Any] = js.native
  var form_errors: js.UndefOr[js.Array[String]] = js.native
}

object ElementCardJs {
  def from(copy: ElementCardJs) = {
    
    js.Dynamic.literal(enabled = copy.enabled, short_name = copy.short_name, summary = copy.summary, row = copy.row, col = copy.col, sizeX = copy.sizeX, sizeY = copy.sizeY, 
                        expandable = copy.expandable, configurable = copy.configurable, deletable = copy.deletable,
                        form_model = JSON.parse(JSON.stringify(copy.form_model)), // extra deep!
                        form_errors = js.Array(),
                        template = copy.template
                        )
      .asInstanceOf[ElementCardJs]    
  }
  
  def apply(row: Int, col: Int, expandable: Boolean, template: ElementTemplateJs): ElementCardJs = {
    js.Dynamic.literal(enabled = true, short_name = template.display_name, summary = "", row = row, col = col, sizeX = 1, sizeY = 1, 
                        expandable = expandable, configurable = true, deletable = true,
                        form_model = JsOption(template.default_model)
                                      .map { x => JSON.parse(JSON.stringify(x)) }
                                      .getOrElse(js.Dynamic.literal().asInstanceOf[js.Dictionary[js.Any]]),
                        form_errors = js.Array(),
                        template = template
                        )
      .asInstanceOf[ElementCardJs]
  }
  def buildDummy(label: String):ElementCardJs =
    js.Dynamic.literal(enabled = true, short_name = label, summary = "", row = 0, col = 0, sizeX = 1, sizeY = 1, 
                        expandable = false, configurable = false, deletable = false)
      .asInstanceOf[ElementCardJs]
    
}



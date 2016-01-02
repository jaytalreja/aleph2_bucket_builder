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

import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.annotation.JSExport

@js.native
/** The JS object (JSON) that represents an element builder
 * @author alex
 */
trait ElementTemplateJs extends js.Object {
    val display_name: String = js.native
    val key: String = js.native
    val categories: js.Array[String] = js.native
    val filters: js.Array[String] = js.native
    val expandable: Boolean = js.native
    val schema: js.Array[FormConfigJs] = js.native
    val form_info: String = js.native
    
    // String versions of the functions
    val validation_function: String = js.native 
    val building_function: String = js.native
    val post_building_function: String = js.native // (for expandable cards)
    val post_validation_function: String = js.native // (for expandable cards)
}

object ElementTemplateJs {
  def apply(
    display_name: String = null,
    key: String = null,
    categories: js.Array[String] = null,
    filters: js.Array[String] = null,
    expandable: Boolean = false,
    schema: js.Array[FormConfigJs] = null,
    form_info: String = null
    ) =
  js.Dynamic.literal(display_name = display_name, key = key, categories = categories, filters = filters,
      expandable = expandable, schema = schema, form_info = form_info
      )
      .asInstanceOf[ElementTemplateJs]
}

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

@js.native
/** Type of elements that populates the element template tree
 * @author alex
 */
trait ElementTemplateNodeJs extends js.Object {
  
  val templateIndex: Int = js.native
  val label: String = js.native
  val category:Boolean
  val childen: js.Array[ElementTemplateNodeJs] = js.native
}

object ElementTemplateNodeJs {
   /** Build child element
   * @param templateIndex
   * @param label
   * @return
   */
  def apply(templateIndex: Int, label: String): ElementTemplateNodeJs = 
    js.Dynamic.literal(templateIndex = templateIndex, label = label, category = false).asInstanceOf[ElementTemplateNodeJs]
  
   /** Build category
   * @param templateIndex
   * @param label
   * @param children
   * @return
   */
  def apply(templateIndex: Int, label: String, children: js.Array[ElementTemplateNodeJs]): ElementTemplateNodeJs = 
      js.Dynamic.literal(templateIndex = templateIndex, label = label, children = children, category = true).asInstanceOf[ElementTemplateNodeJs] 
}

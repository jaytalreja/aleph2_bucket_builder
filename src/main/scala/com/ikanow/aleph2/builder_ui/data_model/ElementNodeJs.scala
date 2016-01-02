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
trait ElementNodeJs extends js.Object {
  val root: Boolean = js.native
  val $parent: ElementNodeJs = js.native
  
  val label: String = js.native
  val children: js.Array[ElementNodeJs] = js.native
  
  val element: ElementCardJs = js.native
}

object ElementNodeJs {
  def buildRoot(root_label: String): ElementNodeJs =
    js.Dynamic.literal(root = true, label = root_label, children = js.Array()).asInstanceOf[ElementNodeJs]
  
  def apply(label: String, element: ElementCardJs, parent: ElementNodeJs): ElementNodeJs = 
    js.Dynamic.literal(root = false, label = label, element = element, $parent = parent, children = js.Array()).asInstanceOf[ElementNodeJs]
  
  def apply(label: String, element: ElementCardJs, parent: ElementNodeJs, children: js.Array[ElementNodeJs]): ElementNodeJs = 
    js.Dynamic.literal(root = false, label = label, element = element, $parent = parent, children = children).asInstanceOf[ElementNodeJs] 
}

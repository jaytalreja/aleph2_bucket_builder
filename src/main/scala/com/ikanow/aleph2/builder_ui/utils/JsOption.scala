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
package com.ikanow.aleph2.builder_ui.utils

import com.ikanow.aleph2.builder_ui.data_model._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

/** Utility to handle both null and undefined
 * @author alex
 */
object JsOption {
  def apply[A](maybe: js.UndefOr[A]): Option[A] = maybe.toOption.filter { x => null != x }
  def apply[A <: js.Any](maybe: A): Option[A] = Option(maybe).filter { el => !js.isUndefined(el) }
}
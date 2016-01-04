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
/** Configures the Gridster Grid
 * @author alex
 */
trait GridsterOptionsJs extends js.Object {  
  val minRows: Int = js.native
  val maxRows: Int = js.native
  val columns: Int = js.native
  val colWidth: String = js.native
  val rowHeight: String = js.native
  val margins: js.Array[Int] = js.native
  val defaultSizeX: Int = js.native
  val defaultSizeY: Int = js.native
  val mobileBreakPoint: Int = js.native
  val floating: Boolean = js.native
  
  val resizable: GridsterOptions_ResizableJs = js.native
  val draggable: GridsterOptions_DraggableJs = js.native
  
}

@js.native
/** Configures the Gridster Grid - resize specific
 * @author alex
 */
trait GridsterOptions_ResizableJs extends js.Object {  
  val enabled: Boolean = js.native
}

@js.native
/** Configures the Gridster Grid - drag specific
 * @author alex
 */
trait GridsterOptions_DraggableJs extends js.Object {  
  val enabled: Boolean = js.native
}

object GridsterOptionsJs {
  def apply(pre_resize_or_drag: js.Function0[Unit], 
            post_resize_or_drag: js.Function0[Unit]): GridsterOptionsJs = 
    js.Dynamic.literal(
        minRows = 2,
        maxRows = 100,
        defaultSizeX = 1,
        defaultSizeY = 1,
        floating = false,
        resizable = GridsterOptions_ResizableJs(true, pre_resize_or_drag, post_resize_or_drag),
        draggable = GridsterOptions_DraggableJs(true, pre_resize_or_drag, post_resize_or_drag)
        )
        .asInstanceOf[GridsterOptionsJs]
}

object GridsterOptions_ResizableJs {
  def apply(enabled: Boolean, 
            pre_resize_or_drag: js.Function0[Unit],
            post_resize_or_drag: js.Function0[Unit]): GridsterOptions_ResizableJs = 
    js.Dynamic.literal(
        enabled = enabled,
        start = pre_resize_or_drag,
        stop = post_resize_or_drag
        )
        .asInstanceOf[GridsterOptions_ResizableJs]
}

object GridsterOptions_DraggableJs {
  def apply(enabled: Boolean, 
            pre_resize_or_drag: js.Function0[Unit],
            post_resize_or_drag: js.Function0[Unit]): GridsterOptions_DraggableJs = 
    js.Dynamic.literal(
        enabled = enabled,
        start = pre_resize_or_drag,
        stop = post_resize_or_drag
        )
        .asInstanceOf[GridsterOptions_DraggableJs]
}


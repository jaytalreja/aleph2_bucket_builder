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

import com.ikanow.aleph2.builder_ui.data_model._

/** Retrieves the templates
 * @author alex
 */
@injectable("undoRedoService")
class UndoRedoService {
  
  def registerState(state_change: UndoRedoElement): Unit = {
    val actual_state_change = state_change match {
      case ModifyElement(curr_element, curr_element_again) => {        
        //  def apply(label: String, element: ElementCardJs, parent: ElementNodeJs, children: js.Array[ElementNodeJs]): ElementNodeJs = 

        val copy_of_curr_element = ElementNodeJs(curr_element.label, 
            JSON.parse(JSON.stringify(curr_element.element)).asInstanceOf[ElementCardJs],//(deep copy element) 
            curr_element.parent, curr_element.children)
        ModifyElement(copy_of_curr_element, curr_element)
      }
      case default => state_change
    }
    
    undo_list = actual_state_change :: undo_list
    redo_list = List() // (remove all elements from the redo list)
  }
  
  def restorePrevState(curr_state: ElementNodeJs): Option[UndoRedoElement] = {
    val to_return = undo_list match {
      case head :: tail => {
        undo_list = undo_list.drop(1)
        redo_list = head :: redo_list
        head match {
          case AddElement(added_element) => {
            val index = added_element.parent.children.prefixLength { el => el != added_element }
            if (index < added_element.parent.children.length) {
              added_element.parent.children.remove(index)
            }
          }
          case DeleteElement(deleted_element) => deleted_element.parent.children.push(deleted_element)
          case ModifyElement(old_element, curr_element) => {
            val index = curr_element.parent.children.prefixLength { el => el != curr_element }
            if (index < curr_element.parent.children.length) {
              curr_element.parent.children.remove(index)
              curr_element.parent.children.push(old_element)
            }
          }
        }
        Option(head)
      }
      case default => Option.empty
    }
    to_return
  }

  //TODO redoUndoneState
  
  protected var undo_list: List[UndoRedoElement] = List()
  protected var redo_list: List[UndoRedoElement] = List()
}

@injectable("undoRedoService")
class UndoRedoServiceFactory extends Factory[UndoRedoService] {
  override def apply() = new UndoRedoService
}

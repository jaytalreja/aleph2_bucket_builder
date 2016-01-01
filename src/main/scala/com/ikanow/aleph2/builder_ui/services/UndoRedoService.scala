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

import scala.collection.mutable.ListBuffer

import com.ikanow.aleph2.builder_ui.data_model._

/** Retrieves the templates
 * @author alex
 */
@injectable("undoRedoService")
class UndoRedoService {
  
  def registerState(state_change: UndoRedoElement): Unit = {    
    adjustElement(state_change) +=: undo_list
    redo_list.clear()
  }
  
  def restorePrevState(curr_state: ElementNodeJs): Option[UndoRedoElement] = {
    restoreOrUnrestoreState(curr_state, undo_list, redo_list)
  }
  def redoUndoneState(curr_state: ElementNodeJs): Option[UndoRedoElement] = {
    restoreOrUnrestoreState(curr_state, redo_list, undo_list)
  }

  protected def restoreOrUnrestoreState(curr_state: ElementNodeJs, 
      list1: ListBuffer[UndoRedoElement], list2: ListBuffer[UndoRedoElement]): Option[UndoRedoElement] = {    
    if (list1.isEmpty) {
      Option.empty
    }
    else {
      val head = list1.head
      list1.remove(0)
      reverseElement(head) +=: list2
      mutateState(head, curr_state)
      Option(head)
    }
  }
  
  /** Apply an undo/redo element to the state
 * @param el
 * @param state
 */
protected def mutateState(el: UndoRedoElement, state:ElementNodeJs): Unit = {
      el match {
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
  }
  
  /** For switching between undo and redo lists
 * @param el
 * @return
 */
protected def reverseElement(el: UndoRedoElement): UndoRedoElement = {
      el match {
        case AddElement(added_element) => DeleteElement(added_element)
        case DeleteElement(deleted_element) => AddElement(deleted_element)
        case ModifyElement(old_element, curr_element) => {
          //(if this is here then old_element is now in the global mutable state so just switch them)
          ModifyElement(curr_element, old_element)
        }
      }
  }
  
 /** 
 * @param state_change
 * @return
 */
protected def adjustElement(state_change: UndoRedoElement): UndoRedoElement = {
    state_change match {
      case ModifyElement(curr_element, curr_element_again) => {        
        val copy_of_curr_element = ElementNodeJs(curr_element.label, 
            JSON.parse(JSON.stringify(curr_element.element)).asInstanceOf[ElementCardJs],//(deep copy element) 
            curr_element.parent, curr_element.children)
        ModifyElement(copy_of_curr_element, curr_element)
      }
      case default => state_change
    }    
  }
  
  protected var undo_list: ListBuffer[UndoRedoElement] = ListBuffer()
  protected var redo_list: ListBuffer[UndoRedoElement] = ListBuffer()
}

@injectable("undoRedoService")
class UndoRedoServiceFactory extends Factory[UndoRedoService] {
  override def apply() = new UndoRedoService
}

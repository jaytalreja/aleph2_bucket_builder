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
import scala.scalajs.js.JSApp
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
  
  def registerState(new_root: ElementNodeJs): Unit = {
    undo_list = new_root :: undo_list
    redo_list = List() // (remove all elements from the redo list)
  }
  
  def getPrevState(): Option[ElementNodeJs] = {
    val to_return: Option[ElementNodeJs] = undo_list match {
      case head :: tail => {
        undo_list = undo_list.drop(1)
        redo_list = head :: redo_list
        Option(head)
      }
      case default => Option.empty
    }
    to_return
  }
  
  def getNextState(): Option[ElementNodeJs] = {
    val to_return: Option[ElementNodeJs] = redo_list match {
      case head :: tail => {
        redo_list = redo_list.drop(1)
        Option(head)
      }
      case default => Option.empty
    }
    to_return
  }
  
  protected var undo_list: List[ElementNodeJs] = List()
  protected var redo_list: List[ElementNodeJs] = List()
}

@injectable("undoRedoService")
class UndoRedoServiceFactory extends Factory[UndoRedoService] {
  override def apply() = new UndoRedoService
}

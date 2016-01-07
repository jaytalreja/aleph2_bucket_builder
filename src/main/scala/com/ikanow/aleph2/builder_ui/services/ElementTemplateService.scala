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
import js.JSConverters._
import com.ikanow.aleph2.builder_ui.utils.JsOption

/** Retrieves the templates
 * @author alex
 */
@injectable("elementTemplateService")
class ElementTemplateService(http: HttpService, global_io_service: GlobalInputOutputService) {
  def requestElementTemplates(ideally_from_cache: Boolean): Future[js.Array[ElementTemplateJs]] = {    
    
    if (!ideally_from_cache || (null == cache)) {
      cache = global_io_service.template_url().map { url => 
        http.get[js.Any](url)
          .map { js => global_io_service.template_conversion_fn(js)
                        .map { js => js.asInstanceOf[ElementTemplateJs] } }
        }
        .getOrElse(Future.successful(js.Array()))
    }
    cache
  }
  var cache: Future[js.Array[ElementTemplateJs]] = null
}

@injectable("elementTemplateService")
class ElementTemplateServiceFactory(http: HttpService, global_io_service: GlobalInputOutputService) extends Factory[ElementTemplateService] {
  override def apply() = new ElementTemplateService(http, global_io_service)
}

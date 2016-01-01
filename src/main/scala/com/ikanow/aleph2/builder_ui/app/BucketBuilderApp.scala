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

package com.ikanow.aleph2.builder_ui.app

import com.greencatsoft.angularjs.core._
import scala.scalajs.js.JSApp
import com.greencatsoft.angularjs._
import scala.scalajs.js.annotation.JSExport
import com.ikanow.aleph2.builder_ui.controllers._
import com.ikanow.aleph2.builder_ui.services._

@JSExport
/** Main Angular app declaration
 * @author alex
 */
object BucketBuilderApp extends JSApp {

	def main(): Unit = {
			val module = Angular.module("aleph2_bucket_builder",Seq(
			    "ngRoute", "ngSanitize"
			      ,
	  		    "ui.bootstrap"
  			    , 
			      "treeControl"
  			    , 
	  		    "gridster"
  			    , 
  			    "formly", "formlyBootstrap"
			    ));
			
			// Legacy property method - move over to the c'tor method
			module
  			.controller(BucketBuilderController)
  			.factory[ElementServiceFactory]
  			.factory[ElementTemplateServiceFactory]
  			.factory[UndoRedoServiceFactory]
  			.config(BucketBuilderRouter)

  		// Preferred c'tor method
			module
  			.controller[QuickNavigateController]
  			.controller[FormBuilderController]
  			
	}
}

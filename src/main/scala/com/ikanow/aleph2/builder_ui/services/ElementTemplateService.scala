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
@injectable("elementTemplateService")
class ElementTemplateService(http: HttpService) {
  def requestElementTemplates(ideally_from_cache: Boolean): Future[Seq[ElementTemplateBean]] = {    
    
    //TODO: dummy set of elements    
    
    val dummy_output = List(
        // Top level elements
        
        ElementTemplateBean(
            display_name = "Empty Data Schema",
            key = "data_schema",
            categories = List("Metadata"),
            filters = List("Bucket"),
            expandable = true,
            form_info = 
              """
                <p>This is a container for the data schema for the different attributes of the stored data</p>
                <p>It has no attributes of its own - instead expand it using the <a class="glyphicon glyphicon-fullscreen"></a> icon
                and then add the desired attributes from those available.
                </p>
              """    
            ),
            
        ElementTemplateBean(
            display_name = "Analytic Thread",
            key = "analytic_thread",
            categories = List("Analytics"),
            filters = List("Bucket"),
            expandable = true,
            form_info = 
              """
                <p>This is a container for the different analytic jobs that can be performed by the Aleph2 engine</p>
                <p>It has no attributes of its own - instead expand it using the <a class="glyphicon glyphicon-fullscreen"></a> icon
                and then add the desired attributes (jobs, triggers) from those available.
                </p>
              """    
            ),
            
        ElementTemplateBean(
            display_name = "Batch Enrichment Topology",
            key = "batch_enrichment_topology",
            categories = List("Enrichment"),
            filters = List("Bucket"),
            expandable = true,
            form_info = 
              """
                <p>This is a container for the "single topology" batch enrichment that can be performed by the Aleph2 engine</p>
                <p>It has no attributes of its own - instead expand it using the <a class="glyphicon glyphicon-fullscreen"></a> icon
                and then add and configure the desired topologies from those available.
                </p>
              """    
            ),
            
        ElementTemplateBean(
            display_name = "Batch Enrichment Pipeline",
            key = "batch_enrichment_pipeline",
            categories = List("Enrichment"),
            filters = List("Bucket"),
            expandable = true,
            form_info = 
              """
                <p>This is a container for the batch enrichment pipeline that can be composed and executed by the Aleph2 engine</p>
                <p>It has no attributes of its own - instead expand it using the <a class="glyphicon glyphicon-fullscreen"></a> icon
                and then add the desired pipeline elements from those available.
                </p>
              """    
            ),
            
        ElementTemplateBean(
            display_name = "Streaming Enrichment Topology",
            key = "streaming_enrichment_topology",
            categories = List("Enrichment"),
            filters = List("Bucket"),
            expandable = true,
            form_info = 
              """
                <p>This is a container for the "single topology" streaming enrichment that can be performed by the Aleph2 engine</p>
                <p>It has no attributes of its own - instead expand it using the <a class="glyphicon glyphicon-fullscreen"></a> icon
                and then add and configure the desired topologies from those available.
                </p>
              """    
            ),
            
        ElementTemplateBean(
            display_name = "Streaming Enrichment Pipeline",
            key = "streaming_enrichment_pipeline",
            categories = List("Enrichment"),
            filters = List("Bucket"),
            expandable = true,
            form_info = 
              """
                <p>This is a container for the streaming enrichment pipeline that can be composed and executed by the Aleph2 engine</p>
                <p>It has no attributes of its own - instead expand it using the <a class="glyphicon glyphicon-fullscreen"></a> icon
                and then add the desired pipeline elements from those available.
                </p>
              """    
            ),
            
        ElementTemplateBean(
            display_name = "Flume Harvester",
            key = "harvest_flume",
            categories = List("Harvesters"),
            filters = List("Bucket"),
            expandable = true,
            form_info = 
              """
                <p>This is a container for the data schema for the different attributes of the stored data</p>
                <p>It has no attributes of its own - instead expand it using the <a class="glyphicon glyphicon-fullscreen"></a> icon
                and then add the desired inputs, outputs, and processing options from those available.
                </p>
              """    
            ),
            
        // Flume Harvester

        ElementTemplateBean(
            display_name = "CSV Input",
            key = "csv_input",
            categories = List("Inputs"),
            filters = List("Bucket/harvest_flume"),
            expandable = false,
            schema = List(
                FormConfigBean(
                    key = "enabled",
                    `type` = "checkbox",
                    templateOptions = FormConfigTemplateBean(
                        label = "Enabled?"
                        )
                    ),
                FormConfigBean(
                    key = "input_directory",
                    `type` = "input",
                    templateOptions = FormConfigTemplateBean(
                        label = "Input Spool Directory",
                        `type` = "text",
                        placeholder = "Input path",
                        required = true
                        )
                    ),
                FormConfigBean(
                    key = "columns",
                    `type` = "input",
                    templateOptions = FormConfigTemplateBean(
                        label = "Columns",
                        `type` = "textarea",
                        placeholder = "Comma separated list of fields",
                        required = true
                        )
                    )
                ),
            form_info = 
              "Configures an input for the Flume processing"
            ),

        ElementTemplateBean(
            display_name = "Generic Flume Configuration",
            key = "generic_config",
            categories = List("Processing"),
            filters = List("Bucket/harvest_flume"),
            expandable = false,
            schema = List(
                FormConfigBean(
                    key = "enabled",
                    `type` = "checkbox",
                    templateOptions = FormConfigTemplateBean(
                        label = "Enabled?"
                        )
                    ),
                FormConfigBean(
                    key = "use_integrated_input",
                    `type` = "checkbox",
                    templateOptions = FormConfigTemplateBean(
                        label = "Use integrated input elements"
                        )
                    ),
                FormConfigBean(
                    key = "use_integrated_output",
                    `type` = "checkbox",
                    templateOptions = FormConfigTemplateBean(
                        label = "Use integrated output elements"
                        )
                    ),
                FormConfigBean(
                    key = "config",
                    `type` = "input",
                    templateOptions = FormConfigTemplateBean(
                        label = "Flume Configuration",
                        `type` = "textarea",
                        placeholder = "Standard Flume configuration",
                        required = true
                        )
                    )
                ),
            form_info = 
              "Allows generic (non Aleph2 supported) processing, though can re-use Aleph2-supported input and output blocks (TODO restrictions)"
            ),
            
        ElementTemplateBean(
            display_name = "JSON output",
            key = "json_output",
            categories = List("Outputs"),
            filters = List("Bucket/harvest_flume"),
            expandable = false,
            schema = List(
                FormConfigBean(
                    key = "enabled",
                    `type` = "checkbox",
                    templateOptions = FormConfigTemplateBean(
                        label = "Enabled?"
                        )
                    ),
                FormConfigBean(
                    key = "output",
                    `type` = "select",
                    templateOptions = FormConfigTemplateBean(
                        label = "JSON format",
                        options = List(
                            Map("name" -> "Deserialize 'message' field, ignore metadata fields",
                                "value" -> "message_only"),
                            Map("name" -> "Deserialize 'message' field, append metadata fields",
                                "value" -> "message_metadata"),
                            Map("name" -> "Ignore 'message' field, append metadata fields",
                                "value" -> "metadata_only")                                
                            ),
                        required = true
                        )
                    )
                ),
            form_info = 
              "Configures the integrated Aleph2 Flume output"
            ),
            
            
        // Data schema    
            
        ElementTemplateBean(
            display_name = "Empty Data Schema",
            key = "search_index_schema",
            categories = List("Data Schema"),
            filters = List("Bucket/data_schema"),
            expandable = false,
            schema = List(
                FormConfigBean(
                    key = "enabled",
                    `type` = "checkbox",
                    templateOptions = FormConfigTemplateBean(
                        label = "Enabled?"
                        )
                    )
                ),
            form_info = 
              "Defines the searchability attributes of the stored data objects (TODO)"
            )
            
        )
        
    Future.successful(dummy_output)
  }
}

@injectable("elementTemplateService")
class ElementTemplateServiceFactory(http: HttpService) extends Factory[ElementTemplateService] {
  override def apply() = new ElementTemplateService(http)
}

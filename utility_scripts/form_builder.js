
//form_builder.js

//Creates a very simple set of forms out of a hand generated bucket builder JSON

//Usage: form_builder.js <hand_generated.js>

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

// Template JSON objects

var top_level_form = 
{
		"root": true,
		"label": "Template",
		"children": [
		             ]
};

//(Should derive this from form_building_templates.json)
var top_level_child_form = 
{
		"root": false,
		"label": "Template Builder",
		"element": {
			"enabled": true,
			//"short_name": "NAME",
			//"summary": "INFO",
			"row": 0,
			"col": 0,
			"sizeX": 1,
			"sizeY": 1,
			"expandable": true,
			"configurable": true,
			"deletable": true,
			"form_model": {
				//(samples)
//				"expandable": true,
//				"key": "data_service_schema",
//				"sub_keys": "document_schema",
//				"categories": "Schema",
//				"filters": "Bucket/**",
//				"child_filters": "batch_enrichment_meta,enrichment_meta"
			},
			"template": {
				"display_name": "Template Builder",
				"form_info": "Create a new template element",
				"filters": [
				            "Template"
				            ],
				            "expandable": true,
				            "key": "template_builder",
				            "categories": [
				                           "Template"
				                           ],
				                           "schema": [
				                                      {
				                                    	  "key": "_short_name",
				                                    	  "type": "horizontalInput",
				                                    	  "templateOptions": {
				                                    		  "label": "Template Name",
				                                    		  "placeholder": "The template name",
				                                    		  "required": true
				                                    	  }
				                                      },
				                                      {
				                                    	  "key": "_summary",
				                                    	  "type": "horizontalTextArea",
				                                    	  "defaultValue": "<p>Help here</p>",
				                                    	  "templateOptions": {
				                                    		  "label": "Form Info",
				                                    		  "placeholder": "Text or HTML providing help for the user",
				                                    		  "required": true
				                                    	  }
				                                      },
				                                      {
				                                    	  "key": "key",
				                                    	  "type": "horizontalInput",
				                                    	  "templateOptions": {
				                                    		  "label": "Role/Key",
				                                    		  "placeholder": "The role name of the template (used in parent and child filters)",
				                                    		  "required": true
				                                    	  }
				                                      },
				                                      {
				                                    	  "key": "sub_keys",
				                                    	  "type": "horizontalInput",
				                                    	  "templateOptions": {
				                                    		  "label": "Sub-Role/Keys",
				                                    		  "placeholder": "Optional sub-keys used only in child filters",
				                                    		  "required": false
				                                    	  }
				                                      },
				                                      {
				                                    	  "key": "categories",
				                                    	  "type": "horizontalInput",
				                                    	  "templateOptions": {
				                                    		  "label": "Categories",
				                                    		  "placeholder": "Comma-separated list of categories (determines folder name)",
				                                    		  "required": true
				                                    	  }
				                                      },
				                                      {
				                                    	  "key": "filters",
				                                    	  "type": "horizontalInput",
				                                    	  "templateOptions": {
				                                    		  "label": "Parent Filters",
				                                    		  "placeholder": "Comma-separated list of filters determining for which parents this should be displayed",
				                                    		  "required": false
				                                    	  }
				                                      },
				                                      {
				                                    	  "key": "expandable",
				                                    	  "type": "horizontalCheckbox",
				                                    	  "defaultValue": false,
				                                    	  "templateOptions": {
				                                    		  "label": "Allow child elements",
				                                    		  "required": false
				                                    	  }
				                                      },
				                                      {
				                                    	  "key": "child_filters",
				                                    	  "type": "horizontalInput",
				                                    	  "hideExpression": "!model.expandable",
				                                    	  "templateOptions": {
				                                    		  "label": "Child Filters",
				                                    		  "placeholder": "Comma-separated list of filters determining which child elements should be displayed",
				                                    		  "required": false
				                                    	  }
				                                      }
				                                      ],
				                                      "building_function": {
				                                    	  "_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) {\n\t\tvar obj = {}; \n\t\tobj.display_name = template.element.short_name; \n\t\tobj.form_info = template.element.summary;\n\t\ta2_copy(template.element.form_model, obj);\n\t\tobj.categories = a2_csv_to_array(obj.categories);\n\t\tobj.sub_keys = a2_csv_to_array(obj.sub_keys);\n  \t\tif (0 == (obj.sub_keys || []).length) obj.sub_keys = undefined;\n\t\tobj.filters = a2_csv_to_array(obj.filters);\n\t\tobj.child_filters = a2_csv_to_array(obj.child_filters);\n\t\tobj.schema = []; \n\t\tcurr_obj.push(obj); \n\t\treturn obj;  \n}"
				                                      },
				                                      "post_building_function": {
				                                    	  "_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) {\n  \n}"
				                                      }
			}
		},
		"children": [
		             ]
};

//(Should derive this from form_building_templates.json)
var raw_json_file =        
{
		"root": false,
		"label": "Raw Form JSON",
		"element": {
			"enabled": true,
			//"short_name": "NAME",
			//"summary": "INFO",
			// These increment
			//"row": 0,
			//"col": 0,
			"sizeX": 1,
			"sizeY": 1,
			"expandable": false,
			"configurable": true,
			"deletable": true,
			"form_model": {
				//(samples)
				//"json": "[\n\t\t{\n\t\t\t\"key\": \"_short_name\",\n\t\t\t\"type\": \"horizontalInput\",\n\t\t\t\"templateOptions\": {\n\t\t\t\t\"label\": \"Function Name\",\n\t\t\t\t\"placeholder\": \"The function name (Give it a unique name)\",\n\t\t\t\t\"required\": true\n\t\t\t}\t\t\t\n\t\t},\n\t\t{\n\t\t\t\"key\": \"js\",\n\t\t\t\"type\": \"code_input\",\n\t\t\t\"templateOptions\": {\n\t\t\t\t\"label\": \"Global Function\",\n\t\t\t\t\"codemirror\": {\n\t\t\t\t\t\"lineNumbers\": true,\n\t\t\t\t\t\"smartIndent\": true,\n\t\t\t\t\t\"mode\": \"javascript\"\n\t\t\t\t}\n\t\t\t}\n\t\t}\n]"
			},
			"template": {
				"display_name": "Raw Form JSON",
				"key": "template_element",
				"categories": [
				               "Generic"
				               ],
				               "filters": [
				                           "Template/template_builder",
				                           "Template/**/template_builder"
				                           ],
				                           "expandable": false,
				                           "form_info": "Type raw JSON objects into here to insert them into the schema",
				                           "building_function": {
				                        	   "_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) { var obj = eval('(' + template.element.form_model.json + ')'); for (i in obj) curr_obj.schema.push(obj[i]); }"
				                           },
				                           "default_model": {
				                        	   "json": "[\n\t\t{\n\t\t\t\"key\": \"_short_name\",\n\t\t\t\"type\": \"horizontalInput\",\n\t\t\t\"templateOptions\": {\n\t\t\t\t\"label\": \"Function Name\",\n\t\t\t\t\"placeholder\": \"The function name (Give it a unique name)\",\n\t\t\t\t\"required\": true\n\t\t\t}\t\t\t\n\t\t},\n\t\t{\n\t\t\t\"key\": \"js\",\n\t\t\t\"type\": \"code_input\",\n\t\t\t\"templateOptions\": {\n\t\t\t\t\"label\": \"Global Function\",\n\t\t\t\t\"codemirror\": {\n\t\t\t\t\t\"lineNumbers\": true,\n\t\t\t\t\t\"smartIndent\": true,\n\t\t\t\t\t\"mode\": \"javascript\"\n\t\t\t\t}\n\t\t\t}\n\t\t}\n]"
				                           },
				                           "schema": [
				                                      {
				                                    	  "key": "json",
				                                    	  "type": "code_input",
				                                    	  "templateOptions": {
				                                    		  "label": "Raw Form JSON",
				                                    		  "codemirror": {
				                                    			  "lineNumbers": true,
				                                    			  "smartIndent": true,
				                                    			  "mode": "javascript"
				                                    		  }
				                                    	  }
				                                      }
				                                      ]
			}
		},
		"children": []
}

//(Should derive this from form_building_templates.json)
var function_builder_object =        
{
    "root": false,
    "label": "Builder Functions",
    "element": {
      "enabled": true,
		"short_name": "Builder Functions",
		"summary": "Creating the element from JSON",
		"row": 0,
		"col": 0,
		"sizeX": 1,
		"sizeY": 1,
		"expandable": false,
      "deletable": true,
      "form_model": {
    	  //(can get overwritten)
        "pre_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) {\n  \n}",
        "post_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) {\n  \n}"
      },
      "template": {
        "display_name": "Builder Functions",
        "form_info": "<p>Write functions to build the objects and sub-objects based on the fields of the forms</p>\n<p>builder_function runs before the element's children; post_builder_function runs afterwards</p>",
        "filters": [
          "Template/template_builder"
        ],
        "expandable": false,
        "key": "local_function",
        "categories": [
          "Developer"
        ],
        "schema": [
          {
            "key": "pre_fn",
            "type": "code_input",
            "templateOptions": {
              "label": "Building Function",
              "codemirror": {
                "lineNumbers": true,
                "smartIndent": true,
                "mode": "javascript"
              }
            }
          },
          {
            "key": "post_fn",
            "type": "code_input",
            "defaultValue": "{\n}",
            "templateOptions": {
              "label": "Post Building Function",
              "codemirror": {
                "lineNumbers": true,
                "smartIndent": true,
                "mode": "javascript"
              }
            }
          }
        ],
        "default_model": {
          "pre_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) {\n  \n}",
          "post_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) {\n  \n}"
        },
        "building_function": {
          "_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) {\n \tcurr_obj.building_function = {'$fn': template.element.form_model.pre_fn};\n \tcurr_obj.post_building_function = {'$fn': template.element.form_model.post_fn}; \n}"
        },
        "post_building_function": {
          "_fn": "function(errs, template, curr_obj, all_templates, root_obj, hierarchy, rows, cols) {\n  \n}"
        }
      }
    },
    "children": []
  }


/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

var fs = require('fs');

var filename = process.argv[2];

var input_json_array = JSON.parse(fs.readFileSync(filename).toString());

var mutable_top_level_row_col = {
	row: 0,
	col: 0
};

function array_to_csv(array) {	
	return (array || []).join(","); 
}

input_json_array.forEach(function(input_json) {
	
	var copy_of_top_level = JSON.parse(JSON.stringify(top_level_child_form));
	var copy_of_function_builder = JSON.parse(JSON.stringify(function_builder_object));

	//Row/cols
	copy_of_top_level.element.row = mutable_top_level_row_col.row;
	copy_of_top_level.element.col = mutable_top_level_row_col.col;	
	mutable_top_level_row_col.col++;
	if (mutable_top_level_row_col.col > 3) {
		mutable_top_level_row_col.col = 0;
		mutable_top_level_row_col.row++;
	}
	
//	console.log("----------------------------------------------------------------");
//	console.log("HERE: " + JSON.stringify(copy_of_top_level));
	
	// 1) Top level
	
	copy_of_top_level.element.short_name = input_json.display_name;
	copy_of_top_level.element.summary = input_json.form_info;
		
	copy_of_top_level.element.form_model.expandable = input_json.expandable || false;
	copy_of_top_level.element.form_model.key = input_json.key;
	copy_of_top_level.element.form_model.sub_keys = array_to_csv(input_json.sub_keys);
	copy_of_top_level.element.form_model.categories = array_to_csv(input_json.categories);
	copy_of_top_level.element.form_model.filters = array_to_csv(input_json.filters);
	copy_of_top_level.element.form_model.child_filters = array_to_csv(input_json.child_filters);
	
	// 2) Function builder
	
	var added = false;
	if (null != input_json.building_function) {
		added = true;
		copy_of_function_builder.element.form_model.pre_fn = input_json.building_function['$fn'] || input_json.building_function['_fn'];
	}
	if (null != input_json.post_building_function) {
		added = true;
		copy_of_function_builder.element.form_model.post_fn = input_json.post_building_function['$fn'] || input_json.post_building_function['_fn'];
	}
	if (added) copy_of_top_level.children.push(copy_of_function_builder);
	
	// 3) Each of the schema
	
	var mutable_row_col = {
			row: 0,
			col: 1
		};
	
	if (input_json.schema) input_json.schema.forEach(function(schema) {
		
		var copy_of_raw_json = JSON.parse(JSON.stringify(raw_json_file));
		
		//Row/cols
		copy_of_raw_json.element.row = mutable_row_col.row;
		copy_of_raw_json.element.col = mutable_row_col.col;	
		mutable_row_col.col++;
		if (mutable_row_col.col > 3) {
			mutable_row_col.col = 0;
			mutable_row_col.row++;
		}
		
		if ((null != schema.templateOptions) && (null != schema.templateOptions.label)) {
			copy_of_raw_json.element.short_name = schema.templateOptions.label;
		}
		else {
			copy_of_raw_json.element.short_name = (schema.key || 'Raw HTML');
		}
		copy_of_raw_json.element.summary = "Type: " + (schema.type || 'Raw HTML');
		
		copy_of_raw_json.element.form_model.json = JSON.stringify([ schema ])
		
		copy_of_top_level.children.push(copy_of_raw_json);
	});
	
	top_level_form.children.push(copy_of_top_level);
});

//DEBUG
console.log(JSON.stringify(top_level_form, null, "   "));

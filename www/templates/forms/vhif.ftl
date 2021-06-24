<#assign page_title = "Demo">

<#import "/macros/FTL_localize.ftl" as localize />
<#import "/macros/FTL_forms.ftl" as forms/>

<html>
	<head>
	    <#include '/head.ftl'>
	</head>
<body>

	<#include '/navigation.ftl'>
   
   <@forms.start_smartforms_container wrap=8 />
       
       <@forms.start_smartforms_form  
			method="POST"
			action="/"
			id="vhif-form"
		 />
   
			<@forms.start_smartforms_body class="vhif-form-body"/>  

           <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section />
						<@forms.smartforms_header
							text="Valuation of Health Impacts"
							/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />

            <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section />
						<@forms.smartforms_messages
							/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />
             
             <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section />
		                <@forms.smartforms_text_field 
		                		id="task_name"
		 						label="Task Name"
		 						value=""
		 						required=true
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />

			<@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section />
		               	<@forms.smartforms_single_select_json 
		                		id="hif_result_datasets"
		 						label="HIF Result Datasets"
								defaultValueText="Select HIF Result Dataset"
		 						value=""
		 						required=true
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
			<@forms.end_smartforms_row />

          <@forms.smartforms_clone_start
				id = "hif-results"
			/>

            <@forms.start_smartforms_row class="hif-result-row"/>
	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section class="hif-result-section" />
	            		<@forms.start_smartforms_label class="hif-label" name="hif-label[0]" />
		               		HELLO!
	            		<@forms.end_smartforms_label />
	            		<@forms.smartforms_hidden_text_field id="hif-id-0" name="hif-id[0]" />

	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />

	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section />
		               	<@forms.smartforms_multi_select_json 
		                		id="valuations-select"
			                	name="valuations[0]"
		 						label="Valuations"
		 						required=false
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
			<@forms.end_smartforms_row />
		<@forms.smartforms_clone_end />
          
 		<@forms.end_smartforms_section />

   	       <@forms.start_smartforms_footer class="demo-form-footer"/>  
            	<button type="submit" class="button btn-primary submit">Submit</button>
   	       <@forms.end_smartforms_footer />  

   	       <@forms.end_smartforms_body />  
            
             
		<@forms.end_smartforms_form />
        
   <@forms.end_smartforms_container />



 
 <script>
 
	function loadHIFResultDatasetOptions() {
	
		formData = new FormData();
	
		$.ajax({
			type 		: "GET", 
			url 		: "/api/load-hif-result-dataset-options", 
			data 		: formData,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
			$(".hif_result_datasets").empty();
			$(".hif_result_datasets").select2({
				data: data
			})
		})
		.fail(function(data) {
		
			console.log("fail...");
			console.log(data);
	
		})
		.always(function() {
		});
	}
	
	function loadPollutantOptions() {
	
		formData = new FormData();
	
		$.ajax({
			type 		: "GET", 
			url 		: "/api/load-pollutant-options", 
			data 		: formData,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
			
			$(".pollutant").select2({
				data: data
			})
	
		})
		.fail(function(data) {
		
			console.log("fail...");
			console.log(data);
	
		})
		.always(function() {
		});
	}
	
	function loadValuationFunctionsOptions(endpoint_id, valuations_select_name) {
			
		formData = new FormData();
	
		$.ajax({
			type 		: "GET", 
			url 		: "/api/load-valuation-functions", 
			data 		: "endpointId=" + endpoint_id,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
		
		
			if ($("select[name='" + valuations_select_name + "']").hasClass("select2-hidden-accessible")) {
//				console.log("!!! " + valuations_select_name + " already initialized");
//				console.log("!!! need to do some cleanup");

				$("select[name='" + valuations_select_name + "']").val(null).trigger('change');
				$("select[name='" + valuations_select_name + "']").empty();
			} else {
//				console.log("!!! need initialize");
				$("select[name='" + valuations_select_name + "']").select2();
			}
			
		
		
		
		
			$("select[name='" + valuations_select_name + "']").empty();
			$("select[name='" + valuations_select_name + "']").select2({
					data: data
			})
			
		})
		.fail(function(data) {
		
			console.log("fail...");
			console.log(data);
	
		})
		.always(function() {
		});
	}
	
	
	function loadHIFResultFunctions() {
	
		formData = new FormData();

		$.ajax({
			type 		: "GET", 
			url 		: "/api/load-hif-result-functions", 
			data 		: "resultsetId="+$('.hif_result_datasets').select2('data')[0].id,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
			//$(".hif_functions").empty();
			//$(".hif_functions").select2({
			//	data: data
			//})

			$('div.hif-result-row').show();

			cloneyaWrapper = $('div.hif-result-row').closest('div.toclone.clone-widget.cloneya').parent();
			
			$('div.hif-result-row').each(function( index ) {
								
				if (index > 0) {
				
/*				
					if ($("select[name='valuations[" + index + "]']").hasClass("select2-hidden-accessible")) {
	    				console.log("valuations [" + index + "] has been initialized");
	    				
						$("select[name='valuations[" + index + "]']").val(null).trigger('change');
						$("select[name='valuations[" + index + "]']").empty();
					} else {
	    				console.log("initialize valuations [" + index + "]");
						$("select[name='valuations[" + index + "]']").select2();
					}

//					$("select[name='valuations[" + index + "]']").val(null).trigger('change');
//					$("select[name='valuations[" + index + "]']").select2('destroy');
//					$("select[name='valuations[" + index + "]']").select2();
//					$("select[name='valuations[" + index + "]']").empty();
*/
					$( this ).remove();
				}
			});




/*
			$('.smart-select2').each(function( index ) {
				
				if ($( this ).hasClass("select2-hidden-accessible")) {
					console.log("destroying select2: " + index);
					$( this ).select2('destroy');
					$( this ).select2();
				}
			
			});
*/			
			//$('div.hif-result-row').not(':first').remove();
			
//			console.log(data);
			
			for(var i = 0; i < data.length; i++) {
    			var obj = data[i];
				
				if (i > 0) {				
					cloneyaWrapper = $('div.hif-result-row').closest('div.toclone.clone-widget.cloneya').parent();
					cloneyaWrapper.triggerHandler('clone.cloneya',[cloneyaWrapper.children().first()]);
				}

			$('div#hif-results .select2-container').each(function( index ) {
				//console.log(this);
				if ($( this ).hasClass('select2-container--below')) {
					$( this ).remove()
				}
			});
			


/*
				if ($("select[name='valuations[" + i + "]']").hasClass("select2-hidden-accessible")) {
    				console.log("valuations [" + i + "] has been initialized");
    				
					$("select[name='valuations[" + i + "]']").val(null).trigger('change');
					$("select[name='valuations[" + i + "]']").empty();
				} else {
    				console.log("initialize valuations [" + i + "]");
					$("select[name='valuations[" + i + "]']").select2();
				}
*/
			}

			for(var i = 0; i < data.length; i++) {
    			var obj = data[i];
				$('label[name*="hif-label[' + i + ']"]').text(obj.hif_options);
				$('input[name*="hif-id[' + i + ']"]').val(obj.hif_id);
				
				
				loadValuationFunctionsOptions(data[i].hif_endpoint_id, "valuations[" + i + "]");
			}
			
			//console.log(data);
	
		})
		.fail(function(data) {
		
			console.log("fail...");
			console.log(data);
	
		})
		.always(function() {
		});
	}
		
	 $( document ).ready(function() {
	
		$('.hif-form-body .messages-wrapper').hide();

		loadHIFResultDatasetOptions();
		//loadPopulationOptions();

			$('#hif-results').cloneya({
				minimum: 1,
				serializeIndex: true
			})
			.on('after_append.cloneya', function (event, toclone, newclone) {
				//console.log("newclone");
				//console.log(newclone);
				//$(newclone).slideToggle();
				//console.log('finished cloning ' + toclone.attr('name') + ' to ' + newclone.attr('name'));
			})
			.on('after_clone.cloneya', function ( event, toClone, newClone) {
				//console.log("just cloned...");
				//console.log(event);
				//console.log(event.target);
				
				//console.log(newClone);
				//if (!$(keywords_metadata.formId + ' .button.edit').is(":visible")) {
				//	newClone.find(".delete.button").show()
				//}
			});	

		
	    $(' .submit').on('click', function(event){
	  
			parameters = buildvhifJSON();
			
			submitForm(parameters);
				
	 		event.preventDefault();
	    });
	    
	   	$('.hif_result_datasets').on('change', function(event){
	   		if($('.hif_result_datasets').select2('data')[0].id != "") { 
	   			loadHIFResultFunctions();
			}
	    });
	
	});

	function submitForm(parameters) {
			
		$.ajax({
			type 		: "POST", 
			url 		: "/api/v1/tasks", 
			data 		: parameters,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
			
			//console.log(data);
			task_uuid = data.task_uuid;
			
			$('.vhif-form-body .messages-wrapper').show();
			$('.vhif-form-body .messages').text("Your task was submitted. Your task id is " + task_uuid);
	
		})
		.fail(function(data) {
		
			console.log("fail...");
			console.log(data);
	
		})
		.always(function() {
		});
	}


	function buildvhifJSON() {
	
		var vhifJSON = {};
		
		vhifJSON.name = $('#vhif-form input#task_name').val();
		vhifJSON.type = "Valuation";
		vhifJSON.hifResultDatasetId = $('.hif_result_datasets').select2('data')[0].id;
		vhifJSON.variableDatasetId = 1;
		
		
		vhifJSON.functions = [];
	
		$('div.hif-result-row').each(function( index ) {
		
			hif_id = $( this ).find("input[name='hif-id[" + index + "]']");
			
			$(this.id + " select[name='valuations[" + index + "]']").find(':selected').each(function( index ) {
			
				selectedFunction = {}

				selectedFunction.hifId = hif_id.val();
				selectedFunction.vfId = this.value;
				vhifJSON.functions.push(selectedFunction);
			});
		});

		//console.log(JSON.stringify(vhifJSON));
		
		return JSON.stringify(vhifJSON);
	
	}

</script>
</body>
</html>

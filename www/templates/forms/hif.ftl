<#assign page_title = "Demo">

<#import "/macros/FTL_localize.ftl" as localize />
<#import "/macros/FTL_forms.ftl" as forms/>

<html>
	<head>
	    <#include '/head.ftl'>
	</head>
<body>

	<#include '/navigation.ftl'>
   
   <@forms.start_smartforms_container wrap=4 />
       
       <@forms.start_smartforms_form  
			method="POST"
			action="/"
			id="hif-form"
		 />
   
			<@forms.start_smartforms_body class="hif-form-body"/>  

           <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section />
						<@forms.smartforms_header
							text="Estimate Health Impact"
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
		                		id="pollutant"
		 						label="Pollutant"
								defaultValueText="Select Pollutant"
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
		                		id="air_quality_baseline"
		 						label="Air Quality (Baseline)"
								defaultValueText="Select Baseline Air Quality"
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
		                		id="air_quality_scenario"
		 						label="Air Quality (Scenario)"
								defaultValueText="Select Scenario Air Quality"
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
		                		id="population"
		 						label="Population"
								defaultValueText="Select Population Dataset"
		 						value=""
		 						required=true
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />

          <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section />
		               	<@forms.smartforms_multi_select_json 
		                		id="hif_functions"
		 						label="Functions"
		 						tooltip="Select one or more functions"
		 						required=true
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
		  <@forms.end_smartforms_row />


		 
		<@forms.end_smartforms_section />

   	       <@forms.start_smartforms_footer class="demo-form-footer"/>  
            	<button type="submit" class="button btn-primary submit">Submit</button>
   	       <@forms.end_smartforms_footer />  

   	       <@forms.end_smartforms_body />  
            
             
		<@forms.end_smartforms_form />
        
   <@forms.end_smartforms_container />



 
 <script>
 

	function loadAirQualityOptions() {
	
		formData = new FormData();
	
		$.ajax({
			type 		: "GET", 
			url 		: "/api/load-air-quality-options", 
			data 		: "pollutant="+$('.pollutant').select2('data')[0].id,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
			$(".air_quality_baseline").empty();
			$(".air_quality_baseline").select2({
				data: data
			})
			$(".air_quality_scenario").empty();
			$(".air_quality_scenario").select2({
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
	
		function loadPopulationOptions() {
	
		formData = new FormData();
	
		$.ajax({
			type 		: "GET", 
			url 		: "/api/load-population-options", 
			data 		: formData,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
			
			$(".population").select2({
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
	
	
	function loadFunctions() {
	
		formData = new FormData();

		$.ajax({
			type 		: "GET", 
			url 		: "/api/load-functions", 
			data 		: "pollutant="+$('.pollutant').select2('data')[0].id,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
			$(".hif_functions").empty();
			$(".hif_functions").select2({
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
		
	 $( document ).ready(function() {
	
		$('.hif-form-body .messages-wrapper').hide();

		loadPollutantOptions();
		loadPopulationOptions();

		
	    $(' .submit').on('click', function(event){
	  
			parameters = buildHifJSON();
			
			submitForm(parameters);
				
	 		event.preventDefault();
	    });
	    
	   	$('.pollutant').on('change', function(event){
	   		if($('.pollutant').select2('data')[0].id != "") { 
	   			loadAirQualityOptions();
				loadFunctions();
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
			
			console.log(data);
			task_uuid = data.task_uuid;
			
			$('.hif-form-body .messages-wrapper').show();
			$('.hif-form-body .messages').text("Your task was submitted. Your task id is " + task_uuid);
	
		})
		.fail(function(data) {
		
			console.log("fail...");
			console.log(data);
	
		})
		.always(function() {
		});
	}


	function buildHifJSON() {
	
		var hifJSON = {};
		
		hifJSON.name = $('#hif-form input#task_name').val();
		hifJSON.type = "HIF";
		
		hifJSON.airQualityData = [];
		
		airQualityBaseline = {};
		airQualityBaseline.type = "baseline";
		airQualityBaseline.id = $('.air_quality_baseline').select2('data')[0].id
		
		hifJSON.airQualityData.push(airQualityBaseline)
		
		airQualityScenario = {};
		airQualityScenario.type = "scenario";
		airQualityScenario.id = $('.air_quality_scenario').select2('data')[0].id
		
		hifJSON.airQualityData.push(airQualityScenario)
		
		population = {};
		population.id = $('.population').select2('data')[0].id;
		//Right now, we only have 2010 and 2020 pop datasets. Hardcoding this for the moment.
		population.year = population.id=='41' ? 2020 : 2010;
		
		hifJSON.population = population;
		
		hifJSON.functions = [];
		
		selectedFunctions = $('.hif_functions').select2('data');
		
		for (var i = 0; i < selectedFunctions.length; i++) {
			selectedFunction = {}
			selectedFunction.id = selectedFunctions[i].id;
			hifJSON.functions.push(selectedFunction);
		}
		
		return JSON.stringify(hifJSON);
	
	}

</script>
</body>
</html>

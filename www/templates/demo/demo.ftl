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
			method="post"
			action="/"
			id="demo-form"
		 />
   
			<@forms.start_smartforms_body class="demo-form-body"/>  

			<@forms.smartforms_tagline
				text="BENCLOUD" 
				/>
 
           <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=4 />
            		<@forms.start_smartforms_section />
		               	<@forms.smartforms_single_select_json 
		                		id="metadata_contact_state"
		 						label="State"
								defaultValueText="Select a State"
		 						tooltip="State or Administrative Area of the address specified above"
		 						value=""
		 						required=true
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />

   	       <@forms.end_smartforms_body />  
            
   	       <@forms.start_smartforms_footer class="demo-form-footer"/>  
            	<button type="submit" class="button btn-primary submit">Submit</button>
   	       <@forms.end_smartforms_footer />  
            
		<@forms.end_smartforms_form />
        
   <@forms.end_smartforms_container />



 
 <script>
 
 $( document ).ready(function() {

	
	function loadStates() {
	
		var aj = new MyAjaxObject();
		
		aj.url = "/api/load-states";
		aj.type = "GET";
			
		aj.call();
		
	}
	
	class MyAjaxObject extends AjaxObject {
	
		successFunction(self) {
		
			var listitems = '';
			for (var i = 0; i < self.data.length; i++) {
				listitems += '<option value=' + self.data[i]["state_abbr"] + '>' + self.data[i]["state_name"] + '</option>';
			}
			$("#metadata_contact_state").append(listitems);
		 }
	
	 	alwaysFunction(self) {
	    	console.log(self.secret + " " + self.secret);
		}
		
	}
	
	loadStates();

    $("#demo-form" + ' .submit').on('click', function(event){
    	alert("We'll add submit later...");
    	
		event.preventDefault();
    });

});





</script>
</body>
</html>

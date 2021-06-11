

<div id="aqs-upload-modal" class="smartforms-modal" role="alert">
	<div class="smartforms-modal-container">
            
		<div class="smartforms-modal-header">
			<h3>Upload Air Quality Surface</h3>
			<a href="#" class="smartforms-modal-close">×</a>            
		</div><!-- .smartforms-modal-header -->
                
		<div class="smartforms-modal-body">
									
  <@forms.start_smartforms_container wrap=12 />
       
       <@forms.start_smartforms_form  
			method="POST"
			action="/"
			id="aqs-upload-form"
			multipart=true
		 />
   
			<@forms.start_smartforms_body class="aqs-upload-form-body"/>  

           <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=12 />
            		<@forms.start_smartforms_section />
						<@forms.smartforms_header
							text="Add a new Air Quality Surface"
							/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />

            <@forms.start_smartforms_row 
				class="messages-wrapper" />
	            <@forms.start_smartforms_column width=12 />
            		<@forms.start_smartforms_section />
						<@forms.smartforms_messages
							/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />
             
             <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=12 />
            		<@forms.start_smartforms_section />
		                <@forms.smartforms_text_field 
		                		id="aqs_name"
		 						label="Name"
		 						value=""
		 						required=true
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />

           <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=12 />
            		<@forms.start_smartforms_section />
		               	<@forms.smartforms_single_select_json 
		                		id="pollutants"
		 						label="Pollutant"
								defaultValueText="Select a Pollutant"
		 						value=""
		 						required=true
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />

           <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=12 />
            		<@forms.start_smartforms_section />
		               	<@forms.smartforms_single_select_json 
		                		id="grids"
		 						label="Grid"
								defaultValueText="Select a Grid"
		 						value=""
		 						required=true
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
            <@forms.end_smartforms_row />


          <@forms.start_smartforms_row />
	            <@forms.start_smartforms_column width=12 />
            		<@forms.start_smartforms_section />
		               	<@forms.smartforms_file_upload 
		                		id="aqs_upload"
		                		upload_name_id="aqs_upload_file"
		                		label="Air Quality Surface CSV File"
		 						button_title="Chose AQS file"
								icon_class = "file-upload-icon"
		 						required=true
		 						file_formats="Only CSV files are allowed"
		 						/>
	            	<@forms.end_smartforms_section />
	            <@forms.end_smartforms_column />
		  <@forms.end_smartforms_row />


		<@forms.smartforms_hidden_text_field 
			id="type"
			value="model"
		/>
		 
		<@forms.end_smartforms_section />

          <@forms.start_smartforms_footer/>  
            	<button type="submit" class="button btn-primary submit cancel-button" onclick="$('#aqs-upload-modal').removeClass('smartforms-modal-visible'); return false;">Cancel</button>
           		<button type="submit" data-btntext-sending="Sending..." class="button btn-primary submit-button" onclick="submitAqsUpload(); return false;"> Submit Form </button>
   	       <@forms.end_smartforms_footer />  

   	       <@forms.end_smartforms_body />  
 
                        
		<@forms.end_smartforms_form />
        
   <@forms.end_smartforms_container />
													
																																																	

        </div><!-- .smartforms-modal-body -->
    </div><!-- .smartforms-modal-container -->
</div>




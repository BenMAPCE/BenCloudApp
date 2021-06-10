

<#macro smartforms_multi_select
 	id
 	name = id
 	label=""
 	class = id
 	options=[]
	values=[]
 	validation=""
 	tooltip=""
 	tooltip_direction="tip-right"
 	required=false
>

   <div class="section">
		<label for="${id}" class="field-label">${label} <#if required><span class="required">*</span></#if></label> 
        <label class="field">
            <select class="${id} smart-select2 <#if validation!="">${validation}</#if>" multiple="multiple">
 				<#list options as option>
 					<#if values?seq_contains(option[0])>
 		                <option value="${option[0]}" selected="selected">${option[1]}</option>
					<#else>
		                <option value="${option[0]}">${option[1]}</option>
 					</#if>
				</#list>
            </select>
  			<#if tooltip != ""><b class="tooltip ${tooltip_direction}"><em> ${tooltip}</em></b></#if>                 
       </label>
    </div>

</#macro>

<#macro smartforms_multi_select_json
 	id
 	name = id
 	label=""
 	class = id
 	validation=""
 	tooltip=""
 	tooltip_direction="tip-right"
 	required=false
>

   <div class="section">
		<label for="${id}" class="field-label">${label} <#if required><span class="required">*</span></#if></label> 
        <label class="field">
			<select class="${id} smart-select2 <#if validation!="">${validation}</#if>" multiple="multiple">
			</select>
  			<#if tooltip != ""><b class="tooltip ${tooltip_direction}"><em> ${tooltip}</em></b></#if>                 
       </label>
    </div>

</#macro>

<#macro smartforms_single_select_json
 	id
 	name = id
 	label=""
 	class = id
 	value=""
 	validation=""
	defaultValueText="Select a value"
 	tooltip=""
 	tooltip_direction="tip-right"
 	required=false
>

   <div class="section">
		<label for="${id}" class="field-label">${label} <#if required><span class="required">*</span></#if></label> 
        <label class="field">
			<select class="${id} smart-select2 <#if validation!="">${validation}</#if>">
            <option value="">${defaultValueText}</option>
			</select>
  			<#if tooltip != ""><b class="tooltip ${tooltip_direction}"><em> ${tooltip}</em></b></#if>                 
       </label>
    </div>

</#macro>

<#macro smartforms_single_select
 	id
 	name = id
 	label=""
 	value=""
	options=[]
	defaultValueText="Select a value"
 	tooltip=""
 	validation=""
 	tooltip_direction="tip-right"
 	required=false
>

	<label for="${id}" class="field-label">${label} <#if required><span class="required">*</span></#if></label> 
	<label class="field select">
        <select id="${id}" name="${name}" class="<#if validation!="">${validation}</#if>" >
            <option value="">${defaultValueText}</option>
            <#list options?keys as optionValue> 
 				<option value="${optionValue}" <#if optionValue == value>selected</#if>>${options[optionValue]}</option>
			</#list> 
        </select>
        <i class="arrow"></i>
		<#if tooltip != ""><b class="tooltip ${tooltip_direction}"><em> ${tooltip}</em></b></#if>                 
   </label>                   

</#macro>

<#macro smartforms_single_select_jsonX
 	id
 	name = id
 	label=""
 	value=""
	options="{[]}"
	defaultValueText="Select a value"
 	tooltip=""
 	validation=""
 	tooltip_direction="tip-right"
 	required=false
>

	<label for="${id}" class="field-label">${label} <#if required><span class="required">*</span></#if></label> 
	<label class="field select">
        <select id="${id}" name="${name}" class="<#if validation!="">${validation}</#if>" >
            <option value="">${defaultValueText}</option>
        </select>
        <i class="arrow"></i>
		<#if tooltip != ""><b class="tooltip ${tooltip_direction}"><em> ${tooltip}</em></b></#if>                 
   </label>                   

</#macro>

<#macro smartforms_text_area
 	id
 	name = id
 	rows=4
 	label = ""
 	icon = ""
 	icon_position = ""
 	hint = ""
 	value=""
 	validation=""
 	tooltip=""
 	tooltip_direction="tip-right"
 	required=false
	>
	
   	<div class="section">
    	<label for="${id}" class="field-label">${label} <#if required><span class="required">*</span></#if></label> 
    	<label class="field  <#if icon_position != "">${icon_position}</#if>">
        	<textarea class="gui-textarea  <#if validation!="">${validation}</#if>" id="${id}" name="${name}"><#if value != "">${value}</#if></textarea>
			<#if tooltip != ""><b class="tooltip ${tooltip_direction}"><em> ${tooltip}</em></b></#if>                 
			<#if icon != ""><span class="field-icon"><i class="fa fa-comments"></i></span></#if>
            <#if hint != ""><span class="input-hint"> <strong>Hint: </strong>${hint}</span></#if>  
        </label>
	</div>

</#macro>

 <#macro smartforms_text_field
 	id
 	name = id
 	label = ""
 	icon = ""
 	icon_position = ""
 	placeholder = ""
 	value=""
 	validation=""
 	tooltip=""
 	tooltip_direction="tip-right"
 	required=false
 	disabled=false
	>
	
	<label for="${id}" class="field-label">${label} <#if required><span class="required">*</span></#if></label>
	<label class="field <#if icon_position != "">${icon_position}</#if>">
    	<input type="text" name="${name}" id="${id}" class="gui-input  <#if required>required</#if> <#if disabled> disabled </#if>" 
    		<#if placeholder != "">placeholder="${placeholder}"</#if> 
    		<#if value != "">value="${value}"</#if>
    		<#if disabled> disabled </#if>
    	>
        <#if tooltip != ""><b class="tooltip ${tooltip_direction}"><em> ${tooltip}</em></b></#if>                 
    	<#if icon != ""><span class="field-icon"><i class="fa ${icon}"></i></span></#if>
   </label>
        
</#macro>  

 <#macro smartforms_hidden_text_field
 	id
 	name = id
 	value=""
	>
    	<input type="hidden" name="${name}" id="${id}" class="gui-input" 
    		<#if value != "">value="${value}"</#if>
    	>
        
</#macro>  

 <#macro smartforms_radio_group 
	field
	ids=[]
	values=[]
	labels=[]		 
	inline=false>
		
	<div class="option-group field">
		<div class="smart-option-group smart-option-list group-vertical"> 	
			<#list values as radioOption>
				<label for="${ids[radioOption?index]}" class="option">
	                <input type="radio" name="${field}" id="${ids[radioOption?index]}" value="${radioOption}" <#if radioOption?index == 0>checked</#if> >
	                <span class="smart-option smart-radio">
	                    <span class="smart-option-ui"> 
	                    	<i class="iconc"></i> <@localize.message key=labels[radioOption?index] />
	                    </span>                                  
	                </span>
				</label>
			</#list>	
		</div>
	</div>		
		
</#macro>  


 <#macro smartforms_date_picker
 	id
 	label = ""
 	name = id 	
 	placeholder = ""
 	tooltip=""
 	tooltip_direction="tip-right"
 	icon = ""
 	value=""
 	validation=""
 	icon_position = "append-picker-icon"
  	required=false
>
 
    <label for="${id}" class="field-label">${label} <#if required><span class="required">*</span></#if></label>
 	<label class="field <#if icon_position != "">${icon_position}"</#if>>
		<input type="text" id="${id}" name="${name}" 
			class="gui-input <#if validation!="">${validation}</#if> <#if required>required</#if>" 
				<#if placeholder != "">placeholder="${placeholder}"</#if> 
				<#if value != "">value="${value}"</#if> >
       <#if tooltip != ""><b class="tooltip ${tooltip_direction}"><em> ${tooltip}</em></b></#if>                 
       <#if icon != ""><span class="field-icon"><i class="fa ${icon}"></i></span></#if>
	</label>
 
</#macro> 


<#macro smartforms_checkbox
 	id
 	name = id
 	checked=false	
 	text = ""
>

    <label class="field option block">
        <input type="checkbox" name="${name}" id="${id}" <#if checked>checked</#if>>
        <span class="checkbox"></span> 
		<span class="checkbox-text">${text}</span>
    </label>
    
</#macro>

<#macro smartforms_clone_start
 	id
 	name = id
>

	<div id="${id}" class="cloneya-wrap">
		<div class="toclone clone-widget cloneya">
			<div class="frm-row">
    
</#macro>

<#macro smartforms_clone_end
>

			</div>
			<a href="#" class="clone button btn-primary"><i class="fa fa-plus"></i></a> 
			<a href="#" class="delete button"><i class="fa fa-minus"></i></a>		
		</div>
	</div>
    
</#macro>




<#macro start_smartforms_row>
	<div class="frm-row">
</#macro>

<#macro end_smartforms_row>
	</div>
</#macro>

<#macro start_smartforms_column
	width = 12
	>
	<div class="colm colm${width}">
</#macro>

<#macro end_smartforms_column>
	</div>
</#macro>

<#macro start_smartforms_section>
	<div class="section">
</#macro>

<#macro end_smartforms_section>
	</div>
</#macro>

<#macro start_smartforms_container
	wrap=4
	>
	<div class="smart-wrap">
		<div class="smart-forms smart-container wrap-${wrap}">
</#macro>

<#macro end_smartforms_container>
		</div>
	</div>
</#macro>

<#macro start_smartforms_form
	method="post"
	action="/"
	id=""
	>
    <form method="${method}" action="${action}" id="${id}">
</#macro>

<#macro end_smartforms_form>
	</form>
</#macro>

<#macro start_smartforms_body
	class=""
	>
    <div class="form-body ${class}">
</#macro>

<#macro end_smartforms_body>
	</div>
</#macro>

<#macro start_smartforms_header
	class=""
	>
	<div class="form-header ${class}">
</#macro>

<#macro end_smartforms_header>
	</div>
</#macro>

<#macro start_smartforms_footer
	class=""
	>
	<div class="form-footer ${class}">
</#macro>

<#macro end_smartforms_footer>
	</div>
</#macro>

<#macro smartforms_tagline
	space_above=20
	space_below=30
	text="No Text Specified" 
	>
	<div class="spacer-t${space_above} spacer-b${space_below}">
		<div class="tagline">
			<span>${text} </span>
		</div>
	</div>
</#macro>

<#macro smartforms_header
	space_above=10
	space_below=10
	text="" 
	>
	<div class="spacer-t${space_above} spacer-b${space_below}">
		<div class="header">
			<span>${text}</span>
		</div>
	</div>
</#macro>

 <#macro smartforms_messages
	space_above=10
	space_below=10
	text="" 
	>
	<div class="messages-wrapper spacer-t${space_above} spacer-b${space_below}">
		<div class="messages">
			<span>${text} </span>
		</div>
	</div>
</#macro>
          
                    
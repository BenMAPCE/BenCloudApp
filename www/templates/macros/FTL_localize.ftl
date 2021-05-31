<#macro message key capitalize=false uppercase=false lowercase=false>
  <#compress>
    <#assign translatedKey>
    	<#attempt>
    		<@liferay_ui["message"] key=key />
    	<#recover>
    		key
    	</#attempt>
    </#assign>
    <#if uppercase>
	  ${translatedKey?upper_case}  	
    <#elseif lowercase>
	  ${translatedKey?lower_case}  	
    <#elseif capitalize>
	  ${translatedKey?capitalize}  	
    <#else>
	  ${translatedKey}  	
    </#if>
  </#compress>
</#macro>

<#assign page_title = "Access Previous Runs">

<html>

<head>
    <#include 'head.ftl'>
</head>

<body>

	<#include 'navigation.ftl'>

	<div class="table-wrapper-scroll-y my-custom-scrollbar" style="clear: both; float: left; padding: 20px 25px 0 25px; height: 100%;">
		<table class="table table-bordered table-striped mb-0" id="scenario-table">
			<thead>
				<tr>
    				<th>Scenario Name</th>
    				<th>Type of Rollback</th>
    				<th>Function</th>
    				<th>VSL Standard</th>
    				<th>Output Type</th>
    				<th width="130px">Download</th>
    				<th> </th>
				</tr>
			</thead>
			<tbody>
				<#list scenarios as scenario>
						<td>${scenario.name}</td>
						<td>${scenario.rollbackTypeName}</td>
						<td>${scenario.functionName}</td>
						<td>${scenario.vslName}</td>					
						<td>${scenario.outputType}</td>
					<#assign fileNamePrefix = "GBDRollback_" + scenario.reportFileName + "_" + scenario.dateString />
					<#if scenario.outputType == "XLS">
						<#assign fileName = fileNamePrefix + ".xlsx" />
						<td>
							<a href="/download/${fileName}">${fileName}</a>
						</td>
					<#else>
						<#assign detailsFileName = fileNamePrefix + "_Details.csv" />
						<#assign summaryFileName = fileNamePrefix + "_Summary.csv" />
						<td>
							<a href="/download/${detailsFileName}">${detailsFileName}</a><br/>
							<a href="/download/${summaryFileName}">${summaryFileName}</a>
						</td>
					</#if>
						<td>
							<button type='button' id="DELETE-${fileNamePrefix}" 
								class='btn btn-outline-secondary btn-sm' 
								onClick='processDeleteButton(this)'>Delete</button>
						</td>
					</tr>
				</#list>
			</tbody>
		</table>
	</div>
</p>

<script>

function processDeleteButton(button) {

	var row = button.closest('tr');
	if ((button.id).startsWith("DELETE-")) {
		var r = confirm("Delete this result?");
		if (r == true) {
			console.log("delete: " + button.id.replace("DELETE-",""));
			$.ajax({
            	method: 'POST',
            	data: button.id.replace("DELETE-",""),
            	url: '/delete-file',
            	success: function(data) {
            		location.reload(true);
            	}
         	});
		}
	}
}

</script>
</body>
</html>

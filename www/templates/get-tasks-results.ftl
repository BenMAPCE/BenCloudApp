<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>BenCloud Online - Tasks Results</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="/css/custom.css">
	<script src="/js/jquery/jquery.min.js"></script>
	<script src="/js/bootstrap/bootstrap.min.js"></script>

	<#assign fileNamePrefix = "dummy" />
	
</head>
<body>

	<#include 'navigation.ftl'>

	<div class="table-wrapper-scroll-y my-custom-scrollbar" style="clear: both; float: left; padding: 20px 25px 0 25px; height: 100%;">
		<table class="table table-bordered table-striped mb-0" id="scenario-table">
			<thead>
				<tr>
    				<th>Name</th>
    				<th>Description</th>
    				<th>Output File Name</th>
    				<th> </th>
				</tr>
			</thead>
			<tbody>
				<#list tasks as task>
						<td>${task.name}</td>
						<td>${task.description}</td>
						<td>${task.outputFileName}</td>
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

<#assign page_title = "Pending Tasks">

<html>
<head>
    <#include '/head.ftl'>
	
</head>
<body>

	<#include '/navigation.ftl'>

<div style="padding: 25px;">

	<div class="datatable-wrapper">
		<table id="pending-datatable" class="table table-striped" style="width:100%">
			<thead>
				<tr>
    				<th>Name</th>
    				<th>Description</th>
    				<th>UUID</th>
    				<th>Submitted</th>
    				<th data-type="@data-sort">Wait Time</th>
    				<th>Status</th>
    				<th data-type="@data-sort">Active Time</th>
    				<th>Percentage</th>
    				<th> </th>
				</tr>
			</thead>
		</table>
	</div>

</div>

<script>

	var getResultsUrl = "/tasks/pending-tasks/data";
		
	var resultsDatatableColumns = 
	[
	    { "data": "task_name",
	    	className : "pending-task-name-column"
	    },
	    { "data": "task_description",
	    	className : "pending-task-description-column"
	    },
	    { "data": "task_uuid",
	    	className : "pending-task-uuid-column"
	    },
	    { "data": "task_submitted_date",
	    	className : "pending-task-submitted-date-column"
	    },
	    { "data":
	    	{
				_:    "task_wait_time.task_wait_time_display",
				sort: "task_wait_time.task_wait_time_seconds"
			},
	    	className : "pending-task-wait-time-column"
	    },
	    { "data": "task_status",
	    	className : "pending-task-status-column",
        	render: function ( data, type, row ) {
         		if (!data) {
         			return "Waiting"
         		} else {
         			return "Active"
         		}
         	}
	    },
	    { "data":
	    	{
				_:    "task_active_time.task_active_time_display",
				sort: "task_active_time.task_active_time_seconds"
			},
	    	className : "pending-task-active-time-column"
	    },
	    { "data": "task_percentage",
	    	className : "pending-task-percentage-column"
	    },
	   	{
			className : "pending-column-ellipsis",
			"bSortable": false,
			"mRender": function(data, type, row){
				return createPendingEllipsis(row);
			},
		}
	];

	$( document ).ready(function() {
	
		if (!$.fn.DataTable.isDataTable('#pending-datatable')) {
	
			resultsDatatable = $('#pending-datatable').DataTable({
				"processing": true,
				"serverSide": false,
				"sServerMethod": "POST",
				"pageLength": 25,
				"ajax": {
					"url": getResultsUrl,
					/*
					"data": function (d, callback, settings) {
						d.length = d.length;
						d.start = d.start;
						d.sortColumn = d.order[0].column;
						d.sortDirection = d.order[0].dir;
						d.searchValue = d.search.value;
					},
					*/		
					'error': function (error) {
						console.log("error processing results");
						console.log(error);
					},
				},
				"paging": true,
				"info": true,
				"columns": resultsDatatableColumns,
				dom: 'Blrf<"custom-toolbar">tip',
				select: {
					style: 'os',
					selector: 'td:first-child'
				},
				buttons: [],
			});

		} else {
			$('#pending-datatable').DataTable().ajax.reload();
		}
	});

	function createPendingEllipsis(row){
		
		var str = '';
/*
		str += '<div class="fileMenu"><ul class="dropDownMenu"><li class="has-children">';
		str += '<button class="btn-link file-menu">';
		str += '<i class="fas fa-ellipsis-v"></i>'
		str += '</button>';
		str += '<ul>';
		str += '<li><button style="width:100%; text-align:left;" class="btn-link" onclick="javascript: showViewModal(' + row.resolution_id + ');  hideResolutionsEllipsisMenu(); return false;">View</button></li>';		

		str += '</ul></li></ul></div>';
*/
		return str;
	}

</script>

</body>
</html>

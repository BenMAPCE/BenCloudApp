<#assign page_title = "Air Quality Surfaces">

<#import "/macros/FTL_localize.ftl" as localize />
<#import "/macros/FTL_forms.ftl" as forms/>

<html>
<head>
    <#include '/head.ftl'>
</head>
<body>

<#include '/navigation.ftl'>

<div style="padding: 25px;">

	<div class="air-quality-surfaces-header">Air Quality Surfaces</div>

	<div class="datatable-wrapper">
		<table id="aqs-datatable" class="table table-striped" style="width:100%">
			<thead>
				<tr>
    				<th>ID</th>
    				<th>Name</th>
    				<th>Grid Definition Name</th>
    				<th>Pollutant Name</th>
    				<th> </th>
				</tr>
			</thead>
			<tfoot>
				<tr>
    				<th></th>
    				<th></th>
    				<th></th>
    				<th></th>
    				<th> </th>
				</tr>
			</tfoot>
		</table>
	</div>

</div>

<#include '/forms/aqs-upload-modal.ftl'>

<script>

	var getAqsUrl = "/air-quality-layers";
		
	var aqsDatatableColumns = 
	[
	    { "data": "id",
	    	className : "aqs-id-column"
	    },
	    { "data": "name",
	    	className : "aqs-name-column"
	    },
	    { "data": "grid_definition_name",
	    	className : "aqs-grid-definition-name-column"
	    },
	    { "data": "pollutant_name",
	    	className : "aqs-pollutant-name-column"
	    },
	   	{
			className : "aqs-column-ellipsis",
			"bSortable": false,
			"mRender": function(data, type, row){
				return createAqsEllipsis(row);
			},
		}
	];

	$( document ).ready(function() {
	
		$(document).mouseup(function (e) {
			hideAqsEllipsisMenu(e);
		});


		if (!$.fn.DataTable.isDataTable('#aqs-datatable')) {
	
			aqsDatatable = $('#aqs-datatable').DataTable({
				"processing": true,
				"serverSide": false,
				"sServerMethod": "GET",
				"pageLength": 25,
				"ajax": {
					"url": getAqsUrl,
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
				initComplete: function () {
					// Apply the search
					this.api().columns().every( function () {
						var that = this;
 
						$( 'input', this.footer() ).on( 'keyup change clear', function () {
 							if ( that.search() !== this.value ) {
								that
									.search( this.value )
                            		.draw();
                    		}
						} );
            		} );
        		},
				"paging": true,
				"info": true,
				"columns": aqsDatatableColumns,
				dom: 'Blrftip',
				select: {
					style: 'os',
					selector: 'td:first-child'
				},
				buttons: [
		            {
		                text: 'Add',
		                action: function ( e, dt, node, config ) {
							$('.aqs-upload-form-body .messages-wrapper').hide();
							$('#aqs-upload-modal').addClass('smartforms-modal-visible')
		                }
		            }
				]
			});

		} else {
			$('#aqs-datatable').DataTable().ajax.reload();
		}
		
		$('#aqs-datatable tfoot th').each( function () {
	        if ($(this).index() > 0 && $(this).index() < 4) {
		        $(this).html( '<input type="text" placeholder="Filter" />' );
	        }
	    } );

	});

	function createAqsEllipsis(row){
		
		var str = '';

		str += '<div class="fileMenu"><ul class="dropDownMenu"><li class="has-children">';
		str += '<button class="btn-link file-menu">';
		str += '<i class="fas fa-ellipsis-v"></i>'
		str += '</button>';
		str += '<ul>';
		str += '<li><button style="width:100%; text-align:left;" class="btn-link" onclick="javascript: downloadAsCSV(' + row.id + ', \'' + row.name + '\');  hideAqsEllipsisMenu(); return false;">Download As CSV</button></li>';		

		str += '</ul></li></ul></div>';

		return str;
	}


	$('#aqs-datatable').on('click', '.fileMenu ul li.has-children > button', function(e){
		e.preventDefault();
		if ($(this).next('ul').css("display") === 'none') {
			$(this).next('ul').stop(true, false, true).fadeIn(300);
		} else {
			$(this).parent().find('ul').fadeOut(300);
		}
		return false;
	});
	
	function hideAqsEllipsisMenu(e) {
	    var container = $(".fileMenu ul li.has-children > ul");
	    if (e == undefined || !container.is(e.target) 
	        && container.has(e.target).length === 0) {
	        container.hide();
	    }
	}
	
	function downloadAsCSV(id, name) {
	
	
		$.ajax({
			type 		: "GET", 
			url 		: "/api/v1/air-quality-data/" + id + "/details", 
			data 		: {},
            dataType	: "text",
			cache		: false,
			processData	: false,
		    contentType	: false,
		    headers		: { 'Accept': 'text/csv', 'Content-Type': 'text/csv' },
		})

		.done(function(data) {
			
			var hiddenElement = document.createElement('a');
			hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(data);
	    	hiddenElement.target = '_blank';
	    	hiddenElement.download = name + ".csv";
	    	hiddenElement.click();
    
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
			
			$(".pollutants").select2({
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
	
	
	function loadGridOptions() {
	
		formData = new FormData();
	
		$.ajax({
			type 		: "GET", 
			url 		: "/api/load-grid-options", 
			data 		: formData,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false
		})
		.done(function(data) {
			
			$(".grids").select2({
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
	
		$('.aqs-upload-form-body .messages-wrapper').hide();

		loadPollutantOptions()
		loadGridOptions()
			
	});

	function submitAqsUpload() {

		event.preventDefault();
		
		var name = $('#aqs-upload-form input#aqs_name').val();
		var type = $('#aqs-upload-form input#type').val();
		var pollutantId = 0;
		var gridId = 0;
		
		selectedPollutant = $('.pollutants').select2('data');
		selectedGrid = $('.grids').select2('data');
		
		for (var i = 0; i < selectedPollutant.length; i++) {
			if (selectedPollutant[i].selected) {
				pollutantId = selectedPollutant[i].id;
			}
		}

		for (var i = 0; i < selectedGrid.length; i++) {
			if (selectedGrid[i].selected) {
				gridId = selectedGrid[i].id;
			}
		}

		var formData = new FormData();
		formData.append('file', $('#aqs_upload').get(0).files[0]);
		formData.append('name', name);
		formData.append('type', type);
		formData.append('pollutantId', pollutantId);
		formData.append('gridId', gridId);
			
		$('#aqs-upload-modal .cancel-button').prop("disabled", true)
		$('#aqs-upload-modal .submit-button').prop("disabled", true)

		$('.aqs-upload-form-body .messages-wrapper').show();
		$('.aqs-upload-form-body .messages').text("Processing your Air Quality Surface upload");

		$.ajax({
			type 		: "POST", 
			url 		: "/api/v1/air-quality-data", 
			data 		: formData,
			dataType 	: "json",
			cache		: false,
			processData	: false,
		    contentType	: false,
			enctype: 'multipart/form-data',

		})
		.done(function(data) {
			
			task_uuid = data.task_uuid;
			
			$('.aqs-upload-form-body .messages').text("Your Air Quality Surface was uploaded.");

			$('#aqs-datatable').DataTable().ajax.reload();
	
		})
		.fail(function(data) {
		
			$('.aqs-upload-form-body .messages').text("Your Air Quality Surface upload failed.");

			console.log("fail...");
			console.log(data);
	
		})
		.always(function() {
			$('#aqs-upload-modal .cancel-button').prop("disabled", false)
			$('#aqs-upload-modal .submit-button').prop("disabled", false)
		});
	}

</script>



</body>
</html>

	function downloadAsCSV(id, name, url) {
		
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
	    	hiddenElement.download = name;
	    	hiddenElement.click();
    
		})
		.fail(function(data) {
		
			console.log("fail...");
			console.log(data);
	
		})
		.always(function() {
		});

	}

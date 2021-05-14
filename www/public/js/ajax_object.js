var ajax_object = {};

(function() {

    var self = this;

	this.rowNumber = 0;
	this.formData = new FormData();

	this.dataType = "json";
	this.type = "POST";	
	this.url = "";
	this.cache = false;
	this.processData = false;
	this.contentType = false;
	this.preventDefault = true;

	this.successCallback = "";
	this.alwaysCallback = "";
	
	this.init = function() {
		self.formData = new FormData();
	}
	
	this.call = function() {

		$.ajax({
			type 		: self.type, 
			url 		: self.url, 
			data 		: self.formData,
			dataType 	: self.dataType,
    		cache: self.cache,
			processData: self.processData,
		    contentType: self.contentType
		})
		.done(function(data) {
			
			console.log("done...");
			
			if (self.successCallback != "") {
				console.log("successCallback...");
				self.callback(self.successCallback);
			}
			
			if ( ! data.success) {

			} else {

			}
		})
		.fail(function(data) {
		
			console.log("fail...");
		
		})
		.always(function() {
			
			console.log("always...");
			if (self.alwaysCallback != "") {
				console.log("alwaysCallback...");
				self.callback(self.alwaysCallback);
			}
		
		});

		if (self.preventDefault) {
			event.preventDefault();
		}
	};

	this.addFormData = function(itemName, itemValue) {
		(self.formData).append(itemName, itemValue);
	}
	
	this.callback = function(functionName) {
		console.log("callback: " + functionName);
		functionName();
	}
}).apply(ajax_object);



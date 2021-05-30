
class AjaxObject {

	constructor() {
	
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
		
		this.secret = "SECRET";
	}

	init() {
		this.formData = new FormData();
	}

	call() {

		self = this;
		
		$.ajax({
			type 		: this.type, 
			url 		: this.url, 
			data 		: this.formData,
			dataType 	: this.dataType,
    		cache: this.cache,
			processData: this.processData,
		    contentType: this.contentType
		})
		.done(function(data) {
			
			console.log("done...");
			
			self.successFunction(self);
			
			if ( ! data.success) {

			} else {
				console.log(data.error_message);
			}
		})
		.fail(function(data) {
		
			console.log("fail...");

			self.failFunction(self);
		
		})
		.always(function() {
			
			console.log("always...");
	
			self.alwaysFunction(self);
		
		});

		if (this.preventDefault) {
			event.preventDefault();
		}
	};
	
	addFormData(itemName, itemValue) {
		(this.formData).append(itemName, itemValue);
	}
	
	callFunction(functionName) {
		console.log("callback: " + functionName);
		functionName();
	}

	successFunction(self) {
	}

	alwaysFunction(self) {
	}

	failFunction(self) {
	}
	
	
}

<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>BenCloud Online - Home</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="/css/custom.css">
	<script src="/js/jquery/jquery.min.js"></script>
	<script src="/js/bootstrap/bootstrap.min.js"></script>
	<script src="/js/ajax_object.js"></script>
	<script src="/js/ajax_class.js"></script>
</head>
<body>

	<#include 'navigation.ftl'>

    <div class="container-fluid" style="padding: 25px 25px 25px 25px;">

        <div class="row section-title">
			<div class="col-12">
				About the Tool
			</div>
		</div>
 		
 		<div class="btn-group">
  			<button type="button" id="callAjax" class="btn btn-primary">Call Ajax</button>
		</div>
 	</div>

</body>
</html>

<script>

$( document ).ready(function() {

	$('#callAjax').on('click', function (e) {
		testAjax();
	})

	function testAjax() {
	
		var aj = new MyAjaxObject();
		
		aj.url = "/test-ajax";
		aj.addFormData("xxx", "Barrie");
		aj.addFormData("name", "Barrie");
			
		aj.call();
		
	}
	
	class MyAjaxObject extends AjaxObject {
	
		successFunction(self) {
	    	console.log(self.secret + " Barrie!!!");
	 	}
	
	 	alwaysFunction(self) {
	    	console.log(self.secret + " " + self.secret);
		}
		
	}
});



</script>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>BenCloud Online - Logged In</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="/css/custom.css">
	<script src="/js/jquery/jquery.min.js"></script>
	<script src="/js/bootstrap/bootstrap.min.js"></script>
	<script src="/js/js-cookie.js"></script>

</head>
<body>

	<#include 'navigation.ftl'>

	<div>
		You are logged in!
	</div>

<script>

	var bcoUserIdentifier = Cookies.get('bcoUserIdentifier');
	
	console.log("bcoUserIdentifier: " + bcoUserIdentifier);
	
	function setTime(name, value, days) {
		var d = new Date();
		  d.setTime(d.getTime() + (days*24*60*60*1000));
		  var expires = "expires="+ d.toUTCString();
		  document.cookie = name + "=" + value + ";" + expires + ";path=/";
	}
	
	function getCookie(name) {
	    var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	    return v ? v[2] : null;
	}

</script>
</body>
</html>

<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<g:javascript library="jquery" plugin="jquery"/>
		<g:layoutHead/>
		<r:layoutResources />
	</head>
	<body>
		<sec:ifLoggedIn>
		Welcome <sec:username/> - <g:link controller='logout'>Log Out</g:link>
		</sec:ifLoggedIn>
		<sec:ifNotLoggedIn>
		<g:link controller='login' action='auth'>Login</g:link>
		</sec:ifNotLoggedIn>
		<h1>Demo</h1>
		<g:layoutBody/>
		<r:layoutResources />
	</body>
</html>

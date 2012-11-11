<!doctype html>
<html>
<head>
<meta name="layout" content="bootstrap" />
<title>Grails Twitter Bootstrap Scaffolding</title>
<style type="text/css">
h1,h2,h3,h4,li {
	line-height:10px !important;
	font-size:10px !important;
	margin:0 !important;
}
small {
	font-size:8px !important;
}
</style>
</head>

<body>
<div style="float:right"><g:link action="fetch">Fetch</g:link></div>
	<ul>
		<g:each in="${ providers.sort{a,b-> a.name.toLowerCase().compareTo(b.name.toLowerCase())} }"
			var="provider">
			<li><h1>
					<a href="${provider.baseURL}"> ${provider.name}
					</a>
				</h1>
				<ul>
					<g:each
						in="${ provider.series.sort{a,b-> a.name.toLowerCase().compareTo(b.name.toLowerCase())} }"
						var="serie">
						<li><h2>
								${serie.name}
							</h2>
							<ul>
								<g:each
									in="${ serie.seasons.sort{a,b-> a.number.compareTo(b.number)} }"
									var="season">
									<li><h3>
											Season
											${season.number}
										</h3>
										<ul>
											<g:each
												in="${ season.episodes.sort{a,b-> a.number.compareTo(b.number)} }"
												var="episode">
												<li><h4>
														${episode.number}:
														${episode.name}
														<small>(<g:formatDate format="yyyy-MM-dd"
																date="${episode.released}" />)
														</small>
													</h4>
													<ul>
														<g:each
															in="${ episode.downloadInfo.sort{a,b-> a.downloadLink.toLowerCase().compareTo(b.downloadLink.toLowerCase())} }"
															var="info">
															<li><a href="${info.downloadLink}"> ${info.downloadLink}
															</a></li>
														</g:each>
													</ul>
											</g:each>
										</ul></li>
								</g:each>
							</ul></li>
					</g:each>
				</ul></li>
		</g:each>
	</ul>
</body>
</html>
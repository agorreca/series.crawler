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
h1{font-size:15px !important;}
h2{font-size:13px !important;}
h3{font-size:12px !important;}
h4{font-size:11px !important;}
small {
	font-size:8px !important;
	font-style:italic;
}
</style>
</head>

<body>
<div style="right:10px;top:90px;position:fixed"><g:link action="fetch">Fetch</g:link> | <g:link action="clean">Clean</g:link></div>
<ul>
	<g:each in="${model.keySet().sort{a,b-> a.toLowerCase().compareTo(b.toLowerCase())} }" var="serie">
	<li><h1>${serie}</h1>
		<ul>
		<g:each in="${ model.get(serie).keySet().sort{a,b-> a.toLowerCase().compareTo(b.toLowerCase())} }" var="season">
		<li><h2>${season}</h2>
			<ul>
			<g:each in="${ model.get(serie).get(season).keySet().sort{a,b-> a.toLowerCase().compareTo(b.toLowerCase())} }" var="episode">
			<li><h3>${episode}</h3>
			<ul>
				<g:each in="${ model.get(serie).get(season).get(episode)}" var="info">
				<li><a href="${info.link}" target="_blank">${info.link}</a></li>
				</g:each>
			</ul>
			</li>
			</g:each>
		</ul>
		</li>
		</g:each>
	</ul>
	</li>
	</g:each>
</ul>
<%--	<g:if test="${ providers.size() > 0 }">--%>
<%--	<ul>--%>
<%--		<g:each in="${ providers.sort{a,b-> a.name.toLowerCase().compareTo(b.name.toLowerCase())} }"--%>
<%--			var="provider">--%>
<%--			<li><h1>--%>
<%--					<a href="${provider.baseURL}"> ${provider.name}--%>
<%--					</a>--%>
<%--				</h1>--%>
<%--				<g:if test="${ provider.series.size() > 0 }">--%>
<%--				<ul>--%>
<%--					<g:each--%>
<%--						in="${ provider.series.sort{a,b-> a.name.toLowerCase().compareTo(b.name.toLowerCase())} }"--%>
<%--						var="serie">--%>
<%--						<li><h2>--%>
<%--								${serie.name}--%>
<%--							</h2>--%>
<%--							<g:if test="${ serie.seasons.size() > 0 }">--%>
<%--							<ul>--%>
<%--								<g:each--%>
<%--									in="${ serie.seasons.sort{a,b-> a.number.compareTo(b.number)} }"--%>
<%--									var="season">--%>
<%--									<li><h3>--%>
<%--											Season--%>
<%--											${season.number}--%>
<%--										</h3>--%>
<%--										<g:if test="${ season.episodes.size() > 0 }">--%>
<%--										<ul>--%>
<%--											<g:each--%>
<%--												in="${ season.episodes.sort{a,b-> a.number.compareTo(b.number)} }"--%>
<%--												var="episode">--%>
<%--												<li><h4>--%>
<%--														${episode.number}:--%>
<%--														${episode.name}--%>
<%--														<small>(<g:formatDate format="yyyy-MM-dd"--%>
<%--																date="${episode.released}" />)--%>
<%--														</small>--%>
<%--													</h4>--%>
<%--													<g:if test="${ episode.downloadInfo.size() > 0 }">--%>
<%--													<ul>--%>
<%--														<g:set var="lastLink" value="" />--%>
<%--														<g:each in="${ episode.downloadInfo.findAll{ it.downloadLink != null }.sort{a,b-> a.downloadLink.toLowerCase().compareTo(b.downloadLink.toLowerCase())} }" var="info">--%>
<%--														<g:if test="${lastLink != info.downloadLink}">--%>
<%--															<li><a href="${info.downloadLink}"> ${info.downloadLink}--%>
<%--															</a></li>--%>
<%--															<g:set var="lastLink" value="${info.downloadLink}" />--%>
<%--														</g:if>--%>
<%--														</g:each>--%>
<%--													</ul>--%>
<%--												</g:if>--%>
<%--												</li>--%>
<%--											</g:each>--%>
<%--										</ul>--%>
<%--										</g:if>--%>
<%--									</li>--%>
<%--								</g:each>--%>
<%--							</ul>--%>
<%--							</g:if>--%>
<%--						</li>--%>
<%--					</g:each>--%>
<%--				</ul>--%>
<%--				</g:if>--%>
<%--			</li>--%>
<%--		</g:each>--%>
<%--	</ul>--%>
<%--	</g:if>--%>
</body>
</html>
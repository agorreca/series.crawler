class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:'seriesCrawler',action:'show')
		"500"(view:'/error')
	}
}

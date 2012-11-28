package org.series.crawler


//@Secured(['ROLE_USER'])
class HomeController {

	def index() {
		render 'Home'
	}

//	@Secured(['ROLE_ADMIN'])
	def fetch() {
		render 'Fetch'
	}

//	@Secured(['ROLE_ADMIN'])
	def clean() {
		render 'Clean'
	}
}

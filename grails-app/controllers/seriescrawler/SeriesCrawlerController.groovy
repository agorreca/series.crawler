package seriescrawler

import grails.converters.JSON


class SeriesCrawlerController {

	def sites = [new TvLinks()]

	def show() {
		[providers:Provider.all]
	}

	def fetch() {
		sites.each {
			println "Fetching ${it.name()} from ${it.url()} ..."
			it.parse()
		}
		redirect(action: "show")
	}
}

package org.series.crawler

import grails.converters.JSON

import org.series.crawler.site.Cucirca
import org.series.crawler.site.TvLinks

class SeriesCrawlerController {

	def cucirca = new Cucirca()
	def tvLinks = new TvLinks()

	def show() {
//		[providers:[Provider.findByName(tvLinks.name())]]
//		[providers:Provider.all]
		def model = [:]
		def episodeModel = [:]
		def seasonModel = [:]
		def serieModel = [:]
//		def results = DownloadInfo.withCriteria {
//			isNotNull('anotherDomainClass')
//		}
//		DownloadInfo.where {
//			downloadLink != null
//		}.list().each { downloadInfo ->
//			episodeModel.put('number', downloadInfo.episode.number)
//			episodeModel.put('name', downloadInfo.episode.name)
//			episodeModel.put('released', downloadInfo.episode.released)
//			episodeModel.put('')
//		}

		def episodeList = []
		DownloadInfo.where { downloadLink != null }.list().each { it ->
		  episodeModel = [name:it.episode.name,links:it.downloadLink]
		  episodeList << episodeModel
		}

		def list1 = episodeList.groupBy {it.name}
		def list2 = episodeList.groupBy {it.name}.collect { it.value.collectEntries { it } }
		render list2 as JSON

	}

	def fetch() {
		def sites = [tvLinks,cucirca]
//		sites = [cucirca]
		sites = [tvLinks]
		CrawlerUtils.crawlSites(sites)
		render 'LISTO'
//		redirect(action: "show")
	}

	def clean() {
		def count = 0
		Utils.getBannedServers().each { info ->
			DownloadInfo.createCriteria().list { like('downloadLink', "${info}%") }.each {
				it.downloadLink = null
				it.save(failOnError:true)
				count++
			}
		}
		render "${count} registers deleted"
	}
}
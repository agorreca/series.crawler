package org.series.crawler

import java.text.SimpleDateFormat

import org.series.crawler.site.AnimeFLV
import org.series.crawler.site.Cucirca
import org.series.crawler.site.TvLinks

class SeriesCrawlerController {

	def cucirca = new Cucirca()
	def tvLinks = new TvLinks()
	def animeFlv = new AnimeFLV()
//	def seriesToShow = ['Pretty Little Liars', 'Lie To Me', 'The Big Bang Theory','Touch','Dexter','The Vampire Diaries','Once Upon a Time','Alf','Revolution','Homeland','The Booth at the End','How I Met Your Mother']
//	def seriesToShow = ['Lie To Me', 'The Big Bang Theory','Touch','Dexter','The Vampire Diaries','Once Upon a Time','Revolution','Homeland','The Booth at the End','How I Met Your Mother']
	def seriesToShow = ['How I Met Your Mother']

	def show() {
		def model = []
		DownloadInfo.withCriteria {isNotNull('downloadLink')}.each {
			if (it.episode.season.serie.name.toLowerCase() in seriesToShow.collect {it.toLowerCase()}) {
				def formatter = new SimpleDateFormat('( dd-MM-yyyy )')
				def serieReleased = it.episode.season.serie.released ? formatter.format(it.episode.season.serie.released) : ''
				def episodeReleased = it.episode.released ? formatter.format(it.episode.released) : ''
				def episodeNumber = it.episode.number ==~ /\d+/ ? String.format("%03d", it.episode.number as Integer) : ''
				def seasonNumber = it.episode.season.number ==~ /\d+/ ? String.format("%02d", it.episode.season.number as Integer) : ''
				model << [
							link:it.downloadLink,
							episodeName:"${episodeNumber}: ${it.episode.name} ${episodeReleased}",
							seasonNumber:"Season ${seasonNumber}",
							serieName:"${it.episode.season.serie.name} ${serieReleased}"
						 ]
			}
		}
		[model:model.groupBy({it.serieName},{it.seasonNumber},{it.episodeName}).each {serie,a->a.each{season,b->b.each{episode,c->c.each{
			it.remove('serieName')
			it.remove('seasonNumber')
			it.remove('episodeName')
		}}}}]
	}

	def byProvider() {
		[providers:[Provider.findByName('AnimeFLV')]]
	}

	def fetch() {
//		def sites = [tvLinks,animeFlv,cucirca]
		def sites = [tvLinks,cucirca]
//		sites = [cucirca]
//		sites = [tvLinks]
//		sites = [animeFlv]
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
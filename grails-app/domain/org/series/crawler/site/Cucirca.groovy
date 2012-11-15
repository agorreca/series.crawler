package org.series.crawler.site

import org.series.crawler.Episode
import org.series.crawler.Provider
import org.series.crawler.Season
import org.series.crawler.Serie

class Cucirca extends Site {

	def baseURL = 'http://www.cucirca.com/'
	//def seriesToDownload = ['The Big Bang Theory','Touch','Dexter','The Vampire Diaries','Once Upon a Time']
	def name() { 'Cucirca' }
	def url()  { 'http://www.cucirca.com/' }

	@Override
	public Object parse() {
		def provider = Provider.findByBaseURL(baseURL) ?: new Provider(name:name(),baseURL:baseURL,series:[]).save(failOnError:true)
		html(url()).'**'.findAll{ it.@class == 'tv-section'}.each { 
			it.'**'.findAll{ it.name().toLowerCase().equalsIgnoreCase('a') }.each {
				if (keepProcessing) {
					if (it.@href.text()) {
						def name = it.text().trim()
						//if (name in seriesToDownload) {
							def link = it.@href.text()
							log.info "   Serie: ${name} at ${link}"
							processSerie(link, provider, name)
						//}
					}
				}
			}
		}
		provider.save(failOnError:true)
	}

	private void processSerie(String url, Provider provider, String name) {
		if (keepProcessing) {
			def serie = Serie.findByName(name) ?: new Serie(name:name,released:released,seasons:[]).save(failOnError:true)
			html(url).'**'.findAll{ it.@class.text().startsWith('one_half') && it.@class.text().endsWith('flex_column')}.each {
				def season
				if (it.name().equalsIgnoreCase('h2')) {
					season = it.find{it.name().toLowerCase().equalsIgnoreCase('a')}.text().replace('Season ', '') as Integer
					log.info "     < Season ${seasonNumber} >"
					season = Season.findByNumberAndSerie(seasonNumber,serie) ?: new Season(number:seasonNumber,serie:serie,episode:[]).save(failOnError:true)
					serie.seasons << season
				} else if (it.name().equalsIgnoreCase('p')) {
					it.find{it.name().toLowerCase().equalsIgnoreCase('p')}.'*'.findAll{it.name().toLowerCase().equalsIgnoreCase('a') && it.@href.text()}.each { ep ->
						def episodeURL = ep.@href.text()
						def nodeText = ep.text()
						def matcher = nodeText =~ ~/Episode (.*?) (.*?)/
						def number = matcher[0][1] as Integer
						def episodeName = matcher[0][2]
						log.info "    [Episode ${number}: ${episodeName}]"
						processEpisode(provider, season, episodeURL, number, episodeName)
					}
				}
			}
			provider.series << serie.save(failOnError:true)
		}
	}

	private void processEpisode(Provider provider, Season season, String url, Integer number, String name) {
		if (keepProcessing) {
			def episode = Episode.findByNumberAndNameAndSeason(number,name,season) ?: new Episode(number:number,name:name,season:season,released:null,downloadInfo:[]).save(failOnError:true)
			if (episode.downloadInfo.size() <= DOWNLOAD_LINKS_LIMIT) {
				log.info "      Processing episode ${number} at ${url} ..."
				html(url).'**'.findAll{ it.@onclick =~ '.*frameLink.*'}.each{
					def matcher = it.@onclick.text() =~ ~/'(.*?)'/
					if (matcher.find()) {
						def gateway = "${baseURL}/gateway.php?data=${matcher[0][1]}"
						processDownloadInfo(provider,season,episode,gateway)
					}
				}
			}
		}
	}
}
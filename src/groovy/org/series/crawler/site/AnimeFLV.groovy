package org.series.crawler.site

import java.text.SimpleDateFormat

import org.series.crawler.ApplicationContextHolder
import org.series.crawler.DownloadInfo
import org.series.crawler.Episode
import org.series.crawler.Provider
import org.series.crawler.Season
import org.series.crawler.Serie
import org.series.crawler.Utils

class AnimeFLV extends Site {

	def baseURL = 'http://animeflv.net'
	def name() {
		'AnimeFLV'
	}
	def url()  {
		'http://animeflv.net/'
	}
	def seriesToDownload = ['']
	def seriesWithProblems = ['']
	def lastProcessedSerie = seriesWithProblems.last()
	static serieName = ''

	@Override
	public Object parse() {
		def provider = Provider.findByBaseURL(baseURL) ?: new Provider(name:name(),baseURL:baseURL,series:[]).save(failOnError:true)
		html(url()).'**'.find{
			it.@id == 'mamain'
		}.'*'.findAll{
			it.@class == 'holder'
		}.each {
			def type = ''
			switch(it.@id) {
				case 'flvanimes': type = 'Anime'
				break
				case 'flvovas': type = 'OVA'
				break
				case 'flvpelis': type = 'Pelicula'
			}
			it.'**'.findAll{
				it.name().toLowerCase().equalsIgnoreCase('a')
			}.each {
				if (keepProcessing) {
					if (it.@href.text()) {
						serieName = it.text().trim()
						def name = "${type}.${serieName}"
						//						if (name in seriesToDownload && name > lastProcessedSerie) {
						//						if (!(name in seriesWithProblems) && name > lastProcessedSerie) {
						def link = "${baseURL}${it.@href}"
						log.info "   Serie: ${name} at ${link}"
						processSerie(link, provider, name)
						//						}
					}
				}
			}
		}
		provider.save(failOnError:true)
	}

	private void processSerie(String url, Provider provider, String name) {
		def serie = Serie.findByName(name) ?: new Serie(name:name,seasons:[]).save(failOnError:true)
		html(url).'**'.findAll{
			it.@class.text() == 'lcc' && it.name().toLowerCase().equals('a')
		}.each {
			ep ->
			def seasonNumber = 1
			def season = Season.findByNumberAndSerie(seasonNumber,serie) ?: new Season(number:seasonNumber,serie:serie,episode:[]).save(failOnError:true)
			serie.seasons << season
			def episodeURL = "${baseURL}${ep.@href}"
			def nodeText = ep.text().trim()
			def number, episodeName = ''
			def matcher = nodeText =~ ~/Cap.tulo (.*?):? (.*?)$/
			if (!matcher) {
				log.info "NODE TEXT = ${nodeText}"
			}
			number = matcher[0][1]
			if (!matcher[0][2].toLowerCase().equals('de ' + serieName.toLowerCase())) {
				episodeName = matcher[0][2]
			}
			log.info "    [Episode ${number}: ${episodeName}]"
			processEpisode(provider, serie, season, episodeURL, number, episodeName)
		}
		provider.series << serie.save(failOnError:true)
	}

	private void processEpisode(Provider provider, Serie serie, Season season, String url, String number, String name) {
		def episode = Episode.findByNumberAndNameAndSeason(number,name,season) ?: new Episode(number:number,name:name,season:season,released:null,downloadInfo:[]).save(failOnError:true)
		log.info "      Processing episode ${number} [${name}] at ${url} ..."
//		html(url).'**'.findAll{ it.@class.text() == 'subtab_content'}.each{
//			it.'**'.findAll{ it.name().equalsIgnoreCase('IFRAME')}.each {
//				def processServer = Boolean.TRUE
//				def link = it.@src.text() ?: it.@SRC.text()
//				serverURLReplacement.each { replacer ->
//					def matcher = it.@src.text() =~ ~/${replacer.regexp}/
//					if (matcher && processServer) {
//						link = replacer.replace(it.@src.text())
//						processServer = Boolean.FALSE
//					}
//				}
//				processDownloadInfo(provider,season,episode,link)
//			}
//		}
	}

	private void processDownloadInfo(Provider provider, Season season, Episode episode, String link) {
		if (!DownloadInfo.findByDownloadLink(link)) {
			log.info "      --> Download link: ${link} ..."
			episode.downloadInfo << new DownloadInfo(downloadLink:link,episode:episode).save(failOnError:true)
			season.episodes << episode.save(failOnError:true)
			def timestamp = new SimpleDateFormat('yyyy.MM.dd HH:mm:ss').format(new Date())
			def serie = season.serie
			def seasonStr = season.number as String
			def line = "${timestamp}\t${provider.name}\t${serie.name}\tSeason ${seasonStr}\t${episode.name}\t${link}".concat(System.getProperty('line.separator'))
			String filePath = ApplicationContextHolder.grailsApplication.config.downloadInfo.statistics.file.path
				new File(filePath).append(line)
		}
	}
}
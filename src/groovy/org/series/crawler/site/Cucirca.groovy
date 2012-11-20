package org.series.crawler.site

import java.text.SimpleDateFormat

import org.series.crawler.ApplicationContextHolder
import org.series.crawler.DownloadInfo
import org.series.crawler.Episode
import org.series.crawler.Provider
import org.series.crawler.Season
import org.series.crawler.Serie
import org.series.crawler.Utils

class Cucirca extends Site {

	def baseURL = 'http://www.cucirca.com/'
//	def seriesToDownload = ['The Big Bang Theory','Touch','Dexter','The Vampire Diaries','Once Upon a Time']
	def seriesToDownload = ['Dexter']
	def name() { 'Cucirca' }
	def url()  { 'http://www.cucirca.com/' }
	def serverURLReplacement = [
		new ServerURLReplacement('(.*?)embed(.*?)embed.php\\?.*?v=(.*?)&.*?$', '$1www$2video/$3'),
		new ServerURLReplacement('(.*?)player/embed.php\\?.*?v=(.*?)&.*?$','$1v/$2'),
		new ServerURLReplacement('(.*?)embed-(.*?)-.*html$','$1$2')
	]
//	def seriesToDownload = ['Touch','Dexter','The Vampire Diaries','Once Upon a Time', 'The Big Bang Theory']
	def seriesWithProblems = ['How I Met Your Mother', 'One Tree Hill', 'Psych', 'The Simpsons', 'Two and a Half Men']

	class ServerURLReplacement {
		def regexp, replacement
		ServerURLReplacement(String regexp, String replacement) {
			this.regexp = regexp
			this.replacement = replacement
		}
		def replace(String string) {
			string.replaceAll(this.regexp, this.replacement)
		}
	}

	@Override
	public Object parse() {
		def provider = Provider.findByBaseURL(baseURL) ?: new Provider(name:name(),baseURL:baseURL,series:[]).save(failOnError:true)
		html(url()).'**'.findAll{ it.@class == 'tv-section'}.each {
			it.'**'.findAll{ it.name().toLowerCase().equalsIgnoreCase('a') }.each {
				if (keepProcessing) {
					if (it.@href.text()) {
						def name = it.text().trim()
						if (name in seriesToDownload) {
//						if (!(name in seriesWithProblems)) {
							def link = it.@href.text()
							log.info "   Serie: ${name} at ${link}"
							processSerie(link, provider, name)
						}
					}
				}
			}
		}
		provider.save(failOnError:true)
	}

	private void processSerie(String url, Provider provider, String name) {
		def serie = Serie.findByName(name) ?: new Serie(name:name,seasons:[]).save(failOnError:true)
		html(url).'**'.findAll{ it.@class.text().startsWith('one_half') && it.@class.text().endsWith('flex_column')}.each {
			if (it.'*'.size()) {
				def nodeAux = it.'*'.find {it.name().equalsIgnoreCase('h2') || (it.name().equalsIgnoreCase('p') && it.text().contains('Season') && !it.text().contains('Episode'))}
				def nodeAuxText =  nodeAux.text().contains('Season ') ? nodeAux.text() : nodeAux.childNodes().getAt(0).text()
				def seasonNumber = Integer.parseInt(nodeAuxText.replaceAll(/[\D]/,''))
				log.info "     < Season ${seasonNumber} >"
				def season = Season.findByNumberAndSerie(seasonNumber,serie) ?: new Season(number:seasonNumber,serie:serie,episode:[]).save(failOnError:true)
				serie.seasons << season

				it.'*'.find {it.name().equalsIgnoreCase('p') && it.text().contains('Episode')}.'*'.findAll{it.name().equalsIgnoreCase('a')}.each { ep ->
					def episodeURL = ep.@href.text()
					def nodeText = ep.text().trim()
					def number, episodeName
					def matcher = nodeText =~ ~/Episode (.*?) (.*?)$/
					if (!matcher) {
						number = '1'
						episodeName = nodeText
					} else {
						number = matcher[0][1]
						episodeName = matcher[0][2]
					}
					log.info "    [Episode ${number}: ${episodeName}]"
					processEpisode(provider, season, episodeURL, number, episodeName)
				}
			}
		}
		provider.series << serie.save(failOnError:true)
	}

	private void processEpisode(Provider provider, Season season, String url, String number, String name) {
		def episode = Episode.findByNumberAndNameAndSeason(number,name,season) ?: new Episode(number:number,name:name,season:season,released:null,downloadInfo:[]).save(failOnError:true)
		log.info "      Processing episode ${number} [${name}] at ${url} ..."
		html(url).'**'.findAll{ it.@class.text().startsWith('postTabs_divs')}.each{
			it.'*'.findAll{ it.name().equalsIgnoreCase('IFRAME')}.each {
				def processServer = Boolean.TRUE
				def link = it.@src.text()
				serverURLReplacement.each { replacer ->
					def matcher = it.@src.text() =~ ~/${replacer.regexp}/
					if (matcher && processServer) {
						link = replacer.replace(it.@src.text())
						processServer = Boolean.FALSE
					}
				}
				processDownloadInfo(provider,season,episode,link)
			}
		}
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
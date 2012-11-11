package seriescrawler

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.lang.StringUtils;
import org.series.crawler.HTTP

class TvLinks extends Site {

	def seriesToDownload = ['Touch']
//	def seriesToDownload = ['The Big Bang Theory','Touch','Dexter','The Vampire Diaries','Once Upon a Time']
	def baseURL = 'http://www.tv-links.eu'
	def name() {'TvLinks'}
	def url()  {'http://www.tv-links.eu/tv-shows/all_links'}
	def keepProcessing = Boolean.TRUE

	@Override
	def parse() {
		def provider = Provider.findByBaseURL(baseURL) ?: new Provider(name:name(),baseURL:baseURL,series:[]).save(failOnError:true)
		html(url()).'**'.findAll{ it.@class == 'list cfix' && it.@href.text()}.each{
			if (keepProcessing) {
				def name = it.SPAN[0].text().replace('Updated!', '').trim()
				if (name in seriesToDownload) {
					def year = it.SPAN[1].text()
					def link = "${baseURL}${it.@href}"
					def released = StringUtils.isBlank(year) ? null : new SimpleDateFormat('yyyy').parse(year)
					println "Serie: ${name} (${year}) at ${link}"
					processSerie(link, provider, name, released)
				}
			}
		}
		provider.save(failOnError:true)
	}

	private void processSerie(String url, Provider provider, String name, Date released) {
		if (keepProcessing) {
			def serie = Serie.findByNameAndProvider(name,provider) ?: new Serie(provider:provider,name:name,released:released,seasons:[]).save(failOnError:true)
	 		def season
			html(url).'**'.find{ it.@class == 'z_title cfix brd_l_dot'}.'*'.findAll{it.name().equalsIgnoreCase('div') || it.name().equalsIgnoreCase('ul')}.each { 
				if (it.name().equalsIgnoreCase('div') && StringUtils.startsWith(it.@id.text(), 'dv_snr')) {
					def seasonNumber = Integer.parseInt(it.@id.text().replaceAll("[\\D]", ""))
					println "< Season ${seasonNumber} >"
					season = Season.findByNumberAndSerie(seasonNumber,serie) ?: new Season(number:seasonNumber,serie:serie,episode:[]).save(failOnError:true)
					serie.seasons << season
				} else if(it.name().equalsIgnoreCase('ul') && it.@class=='table_shows') {
					it.'**'.findAll{it.@class && it.@class == 'list cfix' && it.@href.text()}.each{ ep ->
						def episodeURL = "${baseURL}${ep.@href}video-results/"
						def number = ep.SPAN[0].text().replace('Episode', '').trim() as Integer
						def episodeName = ep.SPAN[1].text()
						def airDate = ep.SPAN[2].text()
						def releasedDate
						try {releasedDate = new SimpleDateFormat('M/d/yyyy').parse(airDate)} catch (ParseException) {}
						println "  [Episode ${number}: ${episodeName} (${airDate})]"
						processEpisode(season, episodeURL, number, episodeName, releasedDate)
					} 
				}
			}
			provider.series << serie.save(failOnError:true)
		}
	}

	private void processEpisode(Season season, String url, Integer number, String name, Date released) {
		if (keepProcessing) {
			def episode = Episode.findByNumberAndNameAndSeason(number,name,season) ?: new Episode(number:number,name:name,season:season,released:released,downloadInfo:[]).save(failOnError:true)
			println "Processing episode ${number} at ${url} ..."
			html(url).'**'.findAll{ it.@onclick =~ '.*frameLink.*'}.each{
				Pattern pattern = Pattern.compile("'(.*?)'");
				Matcher matcher = pattern.matcher(it.@onclick.text());
				if (matcher.find()) {
					def gateway = "${baseURL}/gateway.php?data=${matcher.group(1)}"
					processDownloadInfo(season,episode,gateway)
				}
			}
		}
	}

	private void processDownloadInfo(Season season, Episode episode, String gateway) {
		if (keepProcessing) {
			if (!DownloadInfo.findByGateway(gateway)) {
				String location = new HTTP().getHeaderField(gateway, 'Location')
				if (location) {
					println "--> Inner link: ${gateway} resolved as ${location} ..."
					def downloadInfo = new DownloadInfo(gateway:gateway,downloadLink:location,episode:episode).save(failOnError:true)
					episode.downloadInfo << 
					season.episodes << episode.save(failOnError:true)
					def timestamp = new SimpleDateFormat('yyyy.MM.dd HH:mm:ss').format(new Date())
					def serie = season.serie
					def seasonStr = season.number as String
					def line = "${timestamp}\t${serie.provider.name}\t${serie.name}\tSeason ${seasonStr}\t${episode.name}\t${location}".concat(System.getProperty('line.separator'))
					new File(grailsApplication.config.downloadInfo.statistics.file.path).append(line)
				} else {
					println "--> Inner link: ${gateway} throws null location header ..."
					keepProcessing = Boolean.FALSE
				}
			}
		}
	}
}
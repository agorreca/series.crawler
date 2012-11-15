package org.series.crawler.site

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.lang.StringUtils
import org.series.crawler.ApplicationContextHolder;
import org.series.crawler.DownloadInfo;
import org.series.crawler.Episode;
import org.series.crawler.Provider;
import org.series.crawler.Season;
import org.series.crawler.Serie;
import org.series.crawler.Utils;

class TvLinks extends Site {

//	def seriesToDownload = ['Touch']
	def seriesToDownload = ['Touch','Dexter','The Vampire Diaries','Once Upon a Time']
//	seriesToDownload << 'The Big Bang Theory'
	def baseURL = 'http://www.tv-links.eu'
	def name() {'TvLinks'}
	def url()  {'http://www.tv-links.eu/tv-shows/all_links'}

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
					log.info "   Serie: ${name} (${year}) at ${link}"
					processSerie(link, provider, name, released)
				}
			}
		}
		provider.save(failOnError:true)
	}

	private void processSerie(String url, Provider provider, String name, Date released) {
		if (keepProcessing) {
			def serie = Serie.findByName(name) ?: new Serie(name:name,released:released,seasons:[]).save(failOnError:true)
	 		def season
			html(url).'**'.find{ it.@class == 'z_title cfix brd_l_dot'}.'*'.findAll{it.name().equalsIgnoreCase('div') || it.name().equalsIgnoreCase('ul')}.each { 
				if (it.name().equalsIgnoreCase('div') && StringUtils.startsWith(it.@id.text(), 'dv_snr')) {
					def seasonNumber = Integer.parseInt(it.@id.text().replaceAll("[\\D]", ""))
					log.info "     < Season ${seasonNumber} >"
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
						log.info "    [Episode ${number}: ${episodeName} (${airDate})]"
						processEpisode(provider, season, episodeURL, number, episodeName, releasedDate)
					} 
				}
			}
			provider.series << serie.save(failOnError:true)
		}
	}

	private void processEpisode(Provider provider, Season season, String url, Integer number, String name, Date released) {
		if (keepProcessing) {
			def episode = Episode.findByNumberAndNameAndSeason(number,name,season) ?: new Episode(number:number,name:name,season:season,released:released,downloadInfo:[]).save(failOnError:true)
			if (episode.downloadInfo.size() <= DOWNLOAD_LINKS_LIMIT) {
				log.info "      Processing episode ${number} at ${url} ..."
				html(url).'**'.findAll{ it.@onclick =~ '.*frameLink.*'}.each{
					Pattern pattern = Pattern.compile("'(.*?)'");
					Matcher matcher = pattern.matcher(it.@onclick.text());
					if (matcher.find()) {
						def gateway = "${baseURL}/gateway.php?data=${matcher.group(1)}"
						processDownloadInfo(provider,season,episode,gateway)
					}
				}
			}
		}
	}

	private void processDownloadInfo(Provider provider, Season season, Episode episode, String gateway) {
		if (keepProcessing) {
			if (!DownloadInfo.findByGateway(gateway)) {
				String location = http.getHeaderField(gateway, 'Location')
				if (location) {
					log.info "      --> Inner link: ${gateway} resolved as ${location} ..."
					def validLocation = !Utils.inBannedServers(location) ? location : null
					def downloadInfo = new DownloadInfo(gateway:gateway,downloadLink:validLocation,episode:episode).save(failOnError:true)
					episode.downloadInfo << 
					season.episodes << episode.save(failOnError:true)
					def timestamp = new SimpleDateFormat('yyyy.MM.dd HH:mm:ss').format(new Date())
					def serie = season.serie
					def seasonStr = season.number as String
					def line = "${timestamp}\t${provider.name}\t${serie.name}\tSeason ${seasonStr}\t${episode.name}\t${location}".concat(System.getProperty('line.separator'))
					String filePath = ApplicationContextHolder.grailsApplication.config.downloadInfo.statistics.file.path
					new File(filePath).append(line)
				} else {
					log.info "      --> Inner link: ${gateway} throws null location header ..."
					keepProcessing = Boolean.FALSE
					http.getNewIdentity()
				}
			}
		}
	}
}
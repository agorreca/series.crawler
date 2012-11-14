package org.series.crawler.site

import java.util.Date;

import org.series.crawler.Provider

class Cucirca extends Site {

	def baseURL = 'http://www.cucirca.com/'
	def seriesToDownload = ['The Big Bang Theory','Touch','Dexter','The Vampire Diaries','Once Upon a Time']
	def name() { 'Cucirca' }
	def url()  { 'http://www.cucirca.com/' }

	@Override
	public Object parse() {
		def provider = Provider.findByBaseURL(baseURL) ?: new Provider(name:name(),baseURL:baseURL,series:[]).save(failOnError:true)
		html(url()).'**'.findAll{ it.@class == 'tv-section'}.findAll{ it.name().equalsIgnoreCase('a') && it.@href.text()}.each{
			def name = it.text().trim()
			if (name in seriesToDownload) {
				def link = it.@href
				log.info "   Serie: ${name} at ${link}"
				processSerie(link, provider, name)
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
						processEpisode(season, episodeURL, number, episodeName, releasedDate)
					}
				}
			}
			provider.series << serie.save(failOnError:true)
		}
	}

}
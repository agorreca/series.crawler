package org.series.crawler.site

import groovy.time.TimeCategory

import org.apache.commons.logging.LogFactory
import org.series.crawler.ApplicationContextHolder
import org.series.crawler.HTTP

abstract class Site {
	protected static final log = LogFactory.getLog(this)
	protected http = new HTTP();
	protected keepProcessing = Boolean.TRUE
	protected DOWNLOAD_LINKS_LIMIT = 9
	protected serverURLReplacement = [
		new ServerURLReplacement('(.*?)embed(.*?)embed.php\\?.*?v=(.*?)&.*?$', '$1www$2video/$3'),
		new ServerURLReplacement('(.*?)player/embed.php\\?.*?v=(.*?)&.*?$','$1v/$2'),
		new ServerURLReplacement('(.*?)embed-(.*?)-.*html$','$1$2')
	]
	protected savePage = Boolean.FALSE

	abstract def name()
	abstract def url()
	abstract def parse()

	def html(def urlStr) {
		def parser = new org.cyberneko.html.parsers.SAXParser()
		parser.setFeature('http://xml.org/sax/features/namespaces', false)
		new XmlSlurper(parser).parseText(htmlText(urlStr))
	}

	synchronized void pause(seconds) {
		//wait 5 seconds before resuming.
		wait(seconds * 1000)
	}

	protected URLToFilename(String urlStr) {
		URLEncoder.encode(urlStr, 'UTF-8')
	}

	protected htmlText(urlStr) {
		def htmlFilePath = ApplicationContextHolder.grailsApplication.config.htmls.folder.concat(URLToFilename(urlStr))
		File file = new File(htmlFilePath)
		use(TimeCategory) {
			def now = new Date()
			if (!file.exists() || savePage || file.lastModified() < (now - 7.days).getTime()) {
				String page = http.getURL(urlStr)
				log.info ' >> Saving page to file'
				file.createNewFile()
				file.write(page)
			}
		}
		file.text
	}
}
package org.series.crawler.site

import org.apache.commons.logging.LogFactory
import org.series.crawler.ApplicationContextHolder
import org.series.crawler.HTTP

abstract class Site {
	protected static final log = LogFactory.getLog(this)
	protected http = new HTTP();
	protected keepProcessing = Boolean.TRUE
	protected DOWNLOAD_LINKS_LIMIT = 3

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
		if (!file.exists()) {
			String page = http.getURL(urlStr)
			log.info ' >> Saving page to file'
			file.createNewFile()
			file.write(page)
		}
		file.text
	}
}
package seriescrawler

import org.series.crawler.HTTP
import org.springframework.beans.factory.annotation.Autowired;

abstract class Site {

	abstract def name()
	abstract def url()
	abstract def parse()
	@Autowired
	def grailsApplication

	def html(def url) {
		def htmlText = new HTTP().getURL(url)
		def parser = new org.cyberneko.html.parsers.SAXParser()
		parser.setFeature('http://xml.org/sax/features/namespaces', false)
		new XmlSlurper(parser).parseText(htmlText)
	}

	synchronized void pause(seconds) {
		//wait 5 seconds before resuming.
		wait(seconds * 1000)
	}
}
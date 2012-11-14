package org.series.crawler

import org.series.crawler.site.Site

class CrawlerUtils {

	public static void crawlSites() {
		CrawlerUtils.crawlSites(Site.all)
	}

	public static void crawlSites(List<Site> sites) {
		sites.each {
			println "Fetching ${it.name()} from ${it.url()} ..."
			it.parse()
		}
	}
}
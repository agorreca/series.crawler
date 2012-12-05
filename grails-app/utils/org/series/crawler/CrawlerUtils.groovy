package org.series.crawler

import groovyx.gpars.GParsPool

import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors

import org.series.crawler.site.Site

class CrawlerUtils {

	static MAX_THREADS = 3
	public static void crawlSites() {
		CrawlerUtils.crawlSites(Site.all)
	}

	static void crawlSites(List<Site> sites) {
//		GParsPool.withPool {
//			sites.eachParallel {
			sites.each {
				println "Fetching ${it.name()} from ${it.url()} ..."
				it.parse()
			}
//		}
	}
}
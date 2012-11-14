package org.series.crawler

import org.series.crawler.site.Cucirca
import org.series.crawler.site.TvLinks

class SeriesCrawlerController {

	def sites = [new TvLinks(), new Cucirca()]

	def show() {
		[providers:Provider.all]
	}

	def fetch() {
		CrawlerUtils.crawlSites(sites)
		redirect(action: "show")
	}

	def clean() {
		def count = 0
		Utils.getBannedServers().each { info ->
			DownloadInfo.createCriteria().list { like('downloadLink', "${info}%") }.each {
				it.downloadLink = null
				it.save(failOnError:true)
				count++
			}
//			count += DownloadInfo.executeUpdate("update DownloadInfo set downloadLink = (:link) where downloadLink like (:info)%", [link:null,info:info])
//			DownloadInfo.where { downloadLink ==~ /^${info}/ }.deleteAll()
//			count += DownloadInfo.createCriteria().list {
//				like('downloadLink', "${info}%")
//			}*.delete()
		}
		redirect(action: "show")
	}
}
package org.series.crawler

import org.apache.commons.logging.LogFactory

class DownloadInfoSchedulerJob {

	protected static final log = LogFactory.getLog(this)
	static triggers = {
		/*
		 CronExpression Explanation
		 s m h D M W Y
		 | | | | | | `- Year [optional]
		 | | | | | `- Day of Week, 1-7 or SUN-SAT, ?
		 | | | | `- Month, 1-12 or JAN-DEC
		 | | | `- Day of Month, 1-31, ?
		 | | `- Hour, 0-23
		 | `- Minute, 0-59
		 `- Second, 0-59
		 */
//		cron name: 'DownloadInfo1', cronExpression: '0 0 0,3,6,9,12,15,18,21 * * ?'
//		cron name: 'DownloadInfo2', cronExpression: '0 30 1,4,7,10,13,16,19,22 * * ?'
		cron name: 'DownloadInfo', cronExpression: '* 0/20/40 * * * ?'
	}

	def execute() {
		def sites = [new TvLinks()]
		sites.each {
			log.info "Fetching ${it.name()} from ${it.url()} ..."
			it.parse()
		}
	}
}

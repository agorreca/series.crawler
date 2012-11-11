package seriescrawler


class DownloadInfoSchedulerJob {

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
		cron name: 'DailyEventsScheduler', cronExpression: '0 */10 * * * ?'
	}

	def execute() {
		def sites = [new TvLinks()]
		sites.each {
			println "Fetching ${it.name()} from ${it.url()} ..."
			it.parse()
		}
	}
}

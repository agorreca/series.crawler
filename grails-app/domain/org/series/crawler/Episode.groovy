package org.series.crawler

class Episode {
	String number
	String name
	Date released

	static hasMany = [downloadInfo: DownloadInfo]
	static belongsTo = [season: Season]
	static constraints = {
		released nullable:true
	}
}
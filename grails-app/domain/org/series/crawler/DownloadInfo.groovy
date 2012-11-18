package org.series.crawler

class DownloadInfo {
	String gateway
	String downloadLink

	static belongsTo = [episode: Episode]
	static constraints = {
		gateway nullable:true
		downloadLink nullable:true
	}
}

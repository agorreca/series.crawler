package org.series.crawler

class DownloadInfo {
	String gateway
	String downloadLink

	static belongsTo = [episode: Episode]
	static constraints = {
		downloadLink nullable:true
	}
}

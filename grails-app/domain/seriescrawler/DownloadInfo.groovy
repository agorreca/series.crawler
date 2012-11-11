package seriescrawler

class DownloadInfo {
	String gateway
	String downloadLink

	static belongsTo = [episode: Episode]
}

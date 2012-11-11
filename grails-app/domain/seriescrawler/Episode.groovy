package seriescrawler

class Episode {
	Integer number
	String name
	Date released

	static hasMany = [downloadInfo: DownloadInfo]
	static belongsTo = [season: Season]
	static constraints = {
		released nullable:true
	}
}
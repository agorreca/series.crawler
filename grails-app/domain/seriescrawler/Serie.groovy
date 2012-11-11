package seriescrawler

class Serie {
	String name
	Date released

	static hasMany = [seasons: Season]
	static belongsTo = [provider: Provider]
	static constraints = {
		released nullable:true
	}
}
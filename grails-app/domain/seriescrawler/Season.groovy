package seriescrawler

class Season {
	Integer number

	static hasMany = [episodes: Episode]
	static belongsTo = [serie: Serie]
}

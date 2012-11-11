package seriescrawler

class Provider {
	String name
	String baseURL

	static hasMany = [series: Serie]
}
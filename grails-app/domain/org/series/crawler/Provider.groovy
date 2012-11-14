package org.series.crawler

class Provider {
	String name
	String baseURL

	static hasMany = [series: Serie]
}
package org.series.crawler

class Serie {
	String name
	Date released

	static hasMany = [seasons: Season]
	static constraints = {
		released nullable:true
	}
}
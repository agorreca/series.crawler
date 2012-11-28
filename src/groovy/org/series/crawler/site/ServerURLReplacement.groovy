package org.series.crawler.site

class ServerURLReplacement {

	def regexp, replacement
	ServerURLReplacement(String regexp, String replacement) {
		this.regexp = regexp
		this.replacement = replacement
	}
	def replace(String string) {
		string.replaceAll(this.regexp, this.replacement)
	}
}
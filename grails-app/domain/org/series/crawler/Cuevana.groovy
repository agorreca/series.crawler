package org.series.crawler

import org.series.crawler.site.Site;

class Cuevana extends Site {

	def name() {'Cuevana'}
	def url()  {'http://www.cuevana.tv/#!/series'}

	@Override
	public Object parse() {
		parser().'**'.findAll{ it.@id == 'serielist'}.each { println it }
	}
}

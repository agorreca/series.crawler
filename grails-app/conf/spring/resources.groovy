import grails.util.Environment

import org.series.crawler.ApplicationContextHolder

beans = {
	applicationContextHolder(ApplicationContextHolder) { bean ->
		bean.factoryMethod = 'getInstance'
	}
}
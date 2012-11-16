package org.series.crawler

import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired

class Utils {

	@Autowired
	static def grailsApplication

	public static def fileToArray(String path) {
		new File(path).text.split(/\s\s/)
	}

	public static def inBannedServers(String location) {
		def inBannedServer = Boolean.FALSE
		Utils.getBannedServers().each {
			if (StringUtils.startsWith(location, it)) 
			inBannedServer = Boolean.TRUE
		}
		inBannedServer
	}

	public static def getBannedServers() {
		fileToArray(ApplicationContextHolder.grailsApplication.config.banned.servers.file.path)
	}
}
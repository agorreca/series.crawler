package org.series.crawler



import grails.test.GrailsUnitTestCase
import grails.test.mixin.*

import org.apache.commons.lang.StringUtils
import org.junit.*
import org.series.crawler.site.Cucirca

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Cucirca)
class CucircaTests {
	def bannedServers = [
		"/tv-shows",
		"http://insidetc.ew.com",
		"http://insidetv.ew.com",
		"http://video.tvguide.com",
		"http://watching-tv.ew.com",
		"http://xfinitytv.comcast.net",
		"http://www.1channel.ch",
		"http://www.aceshowbiz.com",
		"http://www.avclub.com",
		"http://www.cbs.com",
		"http://www.eonline.com",
		"http://www.fanpop.com",
		"http://www.fox.com",
		"http://www.hitfix.com",
		"http://www.hollywoodreporter.com",
		"http://www.huffingtonpost.com",
		"http://www.metacafe.com",
		"http://www.metacritic.com",
		"http://www.movieweb.com",
		"http://www.rottentomatoes.com",
		"http://www.sho.com",
		"http://www.southparkstudios.com",
		"http://www.tv.com",
		"http://www.tvguide.com",
		"http://www.tvrage.com",
		"http://www.youtube.com",
		"http://watch.ctv.ca"
	]
	def locations = [
		"/tv-shows/Dexter_209/season_7/episode_1/",
		"/tv-shows/Dexter_209/season_7/episode_2/",
		"/tv-shows/The-Big-Bang-Theory-_104/season_5/episode_2/",
		"/tv-shows/The-Big-Bang-Theory-_104/season_5/episode_3/",
		"/tv-shows/The-Big-Bang-Theory-_104/season_5/episode_4/",
		"http://allmyvideos.net/0rkjagmw5c99",
		"http://allmyvideos.net/3710od25j6q8",
		"http://allmyvideos.net/5i5u26b22gq1",
		"http://allmyvideos.net/93kj61pjulwk",
		"http://allmyvideos.net/ab56hqj6s6v2",
		"http://allmyvideos.net/i1b839imohcv",
		"http://allmyvideos.net/m1jpbwjbtt8q",
		"http://allmyvideos.net/qv86ow3c206m",
		"http://allmyvideos.net/yqq1h4h4dv1y",
		"http://filenuke.com/vbbjbmq52cf0",
		"http://insidetv.ew.com/2012/05/10/big-bang-theory-finale-simon-helberg/",
		"http://insidetv.ew.com/tag/dexter/",
		"http://movreel.com/80v1dii5cnhp",
		"http://movreel.com/9cgcybpitrg7",
		"http://movreel.com/bq3jd43i676i",
		"http://movreel.com/d37zr4fzq7jr",
		"http://movreel.com/h481q9wpj1ox",
		"http://movreel.com/hgtatf01cp96",
		"http://movreel.com/mv4pzrplya4z",
		"http://movreel.com/qhfiadd2p6nc",
		"http://movreel.com/qxewlqg168zv",
		"http://movreel.com/rkmcy699n5xd",
		"http://movreel.com/su4cvzhddh5n",
		"http://movreel.com/tbbl3u8w647h",
		"http://movreel.com/tcfq0totwyzy",
		"http://muchshare.net/f15ajj7enkjk",
		"http://muchshare.net/jsxxwslt36zt",
		"http://sharerepo.com/0nnmdz9vs7b4",
		"http://sharerepo.com/2rkchfmk1bdp",
		"http://sharerepo.com/ev3cgrm2qdd0",
		"http://sharerepo.com/mce9c5024k4j",
		"http://vidbull.com/1gx51gk7yrg3.html",
		"http://vidbull.com/1n2akm1ylqdg.html",
		"http://vidbull.com/wc1pq5b3e7ys.html",
		"http://vidup.me/4r1njka3vpsh",
		"http://vimeo.com/30948841",
		"http://watch.ctv.ca/the-big-bang-theory/season-5/",
		"http://www.avclub.com/tvclub/tvshow/the-big-bang-theory,59/",
		"http://www.cbs.com/shows/big_bang_theory/episodes/",
		"http://www.fanpop.com/spots/the-big-bang-theory/videos/25356176/title/big-bang-theory-season-5-premiere-clip-2",
		"http://www.filebox.com/wcln1r0sl28p",
		"http://www.filebox.com/wrkgubc8bjwl",
		"http://www.hitfix.com/news/jason-gedrick-manages-a-dexter-season-7-role",
		"http://www.hollywoodreporter.com/live-feed/big-bang-theory-finale-postmortem-howard-bernadette-320776",
		"http://www.hollywoodreporter.com/live-feed/big-bang-theory-finale-wedding-spoilers-320766",
		"http://www.hollywoodreporter.com/live-feed/big-bang-theory-leonard-penny-284192",
		"http://www.hollywoodreporter.com/live-feed/big-bang-theory-leonard-penny-284192",
		"http://www.hollywoodreporter.com/live-feed/big-bang-theory-love-triangles-224605",
		"http://www.hollywoodreporter.com/live-feed/big-bang-theory-love-triangles-224605",
		"http://www.hollywoodreporter.com/live-feed/dexter-finale-scott-buck-deb-finds-out-275303",
		"http://www.huffingtonpost.com/2012/03/12/homeland-season-2-premiere-date_n_1339470.html",
		"http://www.metacafe.com/watch/cb-7a4Hhl_ctrpi3Lu7kiF9xoiuVQGmJ5eg/the_big_bang_theory_behind_the_scenes_season_finale_season_5_episode_23/",
		"http://www.metacafe.com/watch/cb-aEdwSpSQ_A5yIeXmBd3buuqLfRwHJCJw/the_big_bang_theory_change_of_plans_season_5_episode_24/",
		"http://www.metacafe.com/watch/cb-BGu3EgGnnyhF_eaUPlpZdsrAiMYj_sjZ/tv_briefly_the_big_bang_theory_season_5/",
		"http://www.movieweb.com/news/dexter-and-homeland-return-to-showtime-september-30th",
		"http://www.movieweb.com/news/dexter-renewed-for-season-7-and-8-on-showtime",
		"http://www.movreel.com/xaowonspup1e",
		"http://www.nowvideo.eu/video/2474c0b0c0739",
		"http://www.nowvideo.eu/video/503f601751701",
		"http://www.putlocker.com/file/2F87BA6FA5332E7E#",
		"http://www.putlocker.com/file/C89BAE7ED21DC590#",
		"http://www.putlocker.com/file/D8297DB846F53980#",
		"http://www.putlocker.com/file/EF51F61D988E3D19",
		"http://www.putlocker.com/file/FA053C66633B48F1",
		"http://www.sho.com/sho/dexter/season/5/episode/7",
		"http://www.sho.com/sho/dexter/video/season/4",
		"http://www.sockshare.com/file/BAC1605027313268",
		"http://www.sockshare.com/file/EF4C39D44437131C#",
		"http://www.tv.com/shows/the-big-bang-theory/the-beta-test-initiation-1879239/",
		"http://www.tv.com/shows/the-big-bang-theory/the-hawking-excitation-2428726/",
		"http://www.tv.com/shows/the-big-bang-theory/the-rothman-disintegration-2385710/",
		"http://www.tv.com/shows/the-big-bang-theory/the-shiny-trinket-maneuver-1741477/",
		"http://www.tv.com/shows/the-big-bang-theory/the-stag-convergence-2435453/",
		"http://www.tvguide.com/tvshows/the-big-bang-theory/videos/288041",
		"http://www.uploadc.com/2x8zzhkfnmxd",
		"http://www.uploadc.com/46oozzpj03pm",
		"http://www.uploadc.com/85ktaqa3n9k4/First_2_Minutes_Teaser_Dexter_Season_7.mp4.htm",
		"http://www.uploadc.com/q56cgv098ymy",
		"http://www.uploadc.com/r0lre2goxhzd",
		"http://www.vidbux.com/0wus2oo5nfbl",
		"http://www.vidbux.com/afheg01npg8x",
		"http://www.vidbux.com/bbib78a8t4rx",
		"http://www.vidbux.com/ce5t6tnc0shk",
		"http://www.vidbux.com/dc5hn66p1qba",
		"http://www.vidbux.com/dwt9m847e8kk",
		"http://www.vidbux.com/i8uuozm08zdt",
		"http://www.vidbux.com/ik3u5dlm3eli",
		"http://www.vidbux.com/jtsld5lif5hm",
		"http://www.vidbux.com/k996srliy1l8",
		"http://www.vidbux.com/lp77tqudb9t7",
		"http://www.vidbux.com/tw3dizlhzzw2",
		"http://www.vidbux.com/wn6kbewvlxl1",
		"http://www.vidbux.com/wvmqoiohki8a",
		"http://www.vidbux.com/wxwdohptw5ev",
		"http://www.vidxden.com/24kjrod4m2nv",
		"http://www.vidxden.com/3doh9qhxxas2",
		"http://www.vidxden.com/6ecleedl774i",
		"http://www.vidxden.com/6umkppzjvx05",
		"http://www.vidxden.com/7ogn7ha1bfzv",
		"http://www.vidxden.com/8057g2vitzfl",
		"http://www.vidxden.com/bh8weosmuikn",
		"http://www.vidxden.com/lxe2l2bg2rrd",
		"http://www.vidxden.com/mrp1hjuz3mfk",
		"http://www.vidxden.com/pf16su7xhmso",
		"http://www.vidxden.com/s4zxvkstmzvw",
		"http://www.vidxden.com/v50tre2x78zu",
		"http://www.vidxden.com/wcpuxiztt810",
		"http://www.vidxden.com/yemxv6lixxis",
		"http://www.vureel.com/video/47230/The-Big-Bang-Theory-504-GoogleSeo",
		"http://www.vureel.com/video/47240/TheBigBangTheory503-GoogleSEO",
		"http://www.vureel.com/video/51639/Dexter-S7E1--GoogleSEO2",
		"http://www.vureel.com/video/51871/The-Big-Bang-Theory-S06E02--GoogleSEO",
		"http://www.vureel.com/video/52262/TheBigBangTheoryS06E03Dast-GoogleSEOd",
		"http://www.vureel.com/video/52336/The-Big-Bang-Theory--Season-5-Episode-1--GoogleSEO",
		"http://www.vureel.com/video/52623/TheBigBangTheoryS06E04HDTV-GoogleSEO2",
		"http://www.wootly.ch/?v=8K9EEEE4",
		"http://www.wootly.ch/?v=AG9EEEE4",
		"http://www.wootly.ch/?v=OF9EEEE4",
		"http://www.wootly.ch/?v=TY9EEEE4",
		"http://www.youtube.com/watch?v=rzzwNUBc2Z0",
		"http://www.youtube.com/watch?v=xc_7N_wPdgA",
		"http://xfinitytv.comcast.net/tv/The-Big-Bang-Theory/95852/full-episodes",
		"https://allmyvideos.net/fkzdbq9lxu7k",
		"https://allmyvideos.net/oenwmfw3z1g4",
		"https://allmyvideos.net/thhrp3dqrpxi"
	]

	protected def inBannedServers(String location) {
		def inBannedServer = Boolean.FALSE
		bannedServers.each {
			if (StringUtils.startsWith(location, it)) inBannedServer = Boolean.TRUE
		}
		inBannedServer
	}

    void testSomething() {
		locations.each {location ->
			def validLocation = !inBannedServers(location) ? 'YES' : 'NO'
			println "Valid? ${validLocation}  |  ${location}"
		}
		assert true
    }
}

import org.series.crawler.site.Cucirca
import org.series.crawler.site.TvLinks
import org.series.crawler.CrawlerUtils

def tvLinks = new TvLinks()
def cucirca = new Cucirca()
def sites = [tvLinks,cucirca]
//sites = [cucirca]
//sites = [tvLinks]
CrawlerUtils.crawlSites(sites)

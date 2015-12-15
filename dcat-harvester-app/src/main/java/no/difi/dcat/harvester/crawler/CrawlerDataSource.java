package no.difi.dcat.harvester.crawler;

public class CrawlerDataSource {
	
	private final String name;
	private final String url;
	
	public CrawlerDataSource(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return String.format("DataSource {name=%s, url=%s}", name, url);
	}

	public static CrawlerDataSource getDefault() {
		return new CrawlerDataSource(
				"http://dcat.difi.no/npolar", 
				"http://api.npolar.no/dataset/?q=&format=json&variant=dcat&limit=all&filter-links.rel=data&filter-draft=no"
				);
	}
}

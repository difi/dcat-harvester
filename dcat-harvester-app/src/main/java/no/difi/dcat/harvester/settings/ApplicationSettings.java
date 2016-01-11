package no.difi.dcat.harvester.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="application")
public class ApplicationSettings {
	
	private int crawlerThreadPoolSize;
	
	public int getCrawlerThreadPoolSize() {
		return crawlerThreadPoolSize;
	}

	public void setCrawlerThreadPoolSize(int crawlerThreadPoolSize) {
		this.crawlerThreadPoolSize = crawlerThreadPoolSize;
	}
	
}

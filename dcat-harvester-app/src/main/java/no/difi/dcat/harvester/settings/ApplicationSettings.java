package no.difi.dcat.harvester.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="application")
public class ApplicationSettings {
	
	private int crawlerThreadPoolSize;
	
	private String elasticSearchHost;
	private int elasticSearchPort;
	
	public String getElasticSearchHost() {
		return elasticSearchHost;
	}

	public void setElasticSearchHost(String elasticSearchHost) {
		this.elasticSearchHost = elasticSearchHost;
	}

	public int getElasticSearchPort() {
		return elasticSearchPort;
	}

	public void setElasticSearchPort(int elasticSearchPort) {
		this.elasticSearchPort = elasticSearchPort;
	}

	public int getCrawlerThreadPoolSize() {
		return crawlerThreadPoolSize;
	}

	public void setCrawlerThreadPoolSize(int crawlerThreadPoolSize) {
		this.crawlerThreadPoolSize = crawlerThreadPoolSize;
	}
}

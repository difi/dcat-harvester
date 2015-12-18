package no.difi.dcat.admin.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="application")
public class ApplicationSettings {

	private String harvesterUrl;

	public String getHarvesterUrl() {
		return harvesterUrl;
	}

	public void setHarvesterUrl(String harvesterUrl) {
		this.harvesterUrl = harvesterUrl;
	}

	
	
	
}

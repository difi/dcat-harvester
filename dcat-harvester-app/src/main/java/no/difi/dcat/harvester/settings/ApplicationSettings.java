package no.difi.dcat.harvester.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="application")
public class ApplicationSettings {
	
	private String dcatHarvesterCron;
	
	public String getDcatHarvesterCron() {
		return dcatHarvesterCron;
	}
	
	public void setDcatHarvesterCron(String dcatHarvesterCron) {
		this.dcatHarvesterCron = dcatHarvesterCron;
	}
	
}

package no.difi.dcat.harvester;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="test")
public class ApplicationSettings {
	
	private int someInt;
	
	public int getSomeInt() {
		return someInt;
	}
	
	public void setSomeInt(int someInt) {
		this.someInt = someInt;
	}
}

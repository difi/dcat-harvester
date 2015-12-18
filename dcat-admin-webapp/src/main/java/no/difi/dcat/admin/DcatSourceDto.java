package no.difi.dcat.admin;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DcatSourceDto {
	
	@URL
	@NotEmpty
	private final String name;
	
	@URL
	@NotEmpty
	private final String url;
	
	@NotEmpty
	private final String user;
	
	@JsonCreator
	public DcatSourceDto(
			@JsonProperty("name") String name, 
			@JsonProperty("url)") String url, 
			@JsonProperty("user") String user) {
		this.name = name;
		this.url = url;
		this.user = user;
	}
	
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

}

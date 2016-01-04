package no.difi.dcat.admin.web;

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
	private final String description;
	
	
	@URL
	@NotEmpty
	private final String url;
	
	@NotEmpty
	private final String user;
	
	@JsonCreator
	public DcatSourceDto(
			@JsonProperty("name") String name, 
			@JsonProperty("description") String description,
			@JsonProperty("url)") String url, 
			@JsonProperty("user") String user) {
		this.name = name;
		this.description = description;
		this.url = url;
		this.user = user;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

}

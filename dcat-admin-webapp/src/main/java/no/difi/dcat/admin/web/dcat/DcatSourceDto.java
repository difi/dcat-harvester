package no.difi.dcat.admin.web.dcat;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DcatSourceDto {
	
	private final String id;
	
	@NotEmpty
	private final String description;
	
	@URL
	@NotEmpty
	private final String url;
	
	@NotEmpty
	private final String user;
	
	@JsonCreator
	public DcatSourceDto(
			@JsonProperty("id") String id, 
			@JsonProperty("description") String description,
			@JsonProperty("url)") String url, 
			@JsonProperty("user") String user) {
		this.id = id;
		this.description = description;
		this.url = url;
		this.user = user;
	}
	
	public String getId() {
		return id;
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

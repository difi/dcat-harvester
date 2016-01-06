package no.difi.dcat.admin.web.user;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDto {

	@NotEmpty
	private final String username;
	
	@NotEmpty
	private final String password;
	
	@NotEmpty
	private final String role;
	
	@JsonCreator
	public UserDto(
			@JsonProperty("username") String username,
			@JsonProperty("password") String password,
			@JsonProperty("role") String role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getRole() {
		return role;
	}
	
	
	
	
}

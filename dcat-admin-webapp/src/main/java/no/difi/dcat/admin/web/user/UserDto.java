package no.difi.dcat.admin.web.user;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDto {

	private final String userid;
	
	@NotEmpty
	private final String username;
	
	@NotEmpty
	private final String password;
	
	@NotEmpty
	private final String email;
	
	@NotEmpty
	private final String role;
	
	@JsonCreator
	public UserDto(
			@JsonProperty("userid") String userid,
			@JsonProperty("username") String username,
			@JsonProperty("password") String password,
			@JsonProperty("email") String email,
			@JsonProperty("role") String role) {
		this.userid = userid;
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
	}

	public String getId() {
		return userid;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}
}

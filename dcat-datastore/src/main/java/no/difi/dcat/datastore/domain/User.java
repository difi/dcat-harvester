package no.difi.dcat.datastore.domain;

import org.apache.jena.query.QuerySolution;

public class User {

	private String id;
	private String username;
	private String password;
	private String email;
	private String role;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(
			String id,
			String username,
			String password,
			String email,
			String role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
	}

	public String toString() {
		return String.format("user: user_id=%1$s, user_name=%2$s", this.getId(), this.getUsername());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String userid) {
		this.id = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public boolean isAdmin() {
		return role.equalsIgnoreCase("ADMIN");
	}
	
	public boolean isRegularUser() {
		return role.equalsIgnoreCase("USER");
	}
	
	public static User fromQuerySolution(QuerySolution qs) {
		return new User(
				qs.get("userid").asResource().getURI(),
				qs.get("username").asLiteral().getString(),
				qs.get("password").asLiteral().getString(),
				qs.get("email").asLiteral().getString(),
				qs.get("role").asLiteral().getString()
		);
	}

	
}

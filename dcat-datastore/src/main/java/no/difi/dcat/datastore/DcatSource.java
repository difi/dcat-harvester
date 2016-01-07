package no.difi.dcat.datastore;

import org.apache.jena.query.QuerySolution;

import java.util.UUID;

public class DcatSource {

	private String id;
	private String description;
	private String url;
	private String user;

	public DcatSource() {
		// TODO Auto-generated constructor stub
	}

	public DcatSource(
			String id,
			String description,
			String url,
			String user) {
		this.id = id;
		this.description = description;
		this.url = url;
		this.user = user;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public static DcatSource fromQuerySolution(QuerySolution qs) {
		return new DcatSource(
				qs.get("name").asResource().getURI(),
				qs.get("description").asLiteral().getString(),
				qs.get("url").asResource().getURI(),
				qs.get("user").asLiteral().getString()
		);
	}

	public static DcatSource getDefault() {
		return new DcatSource(
				String.format("http://dcat.difi.no/%s", UUID.randomUUID().toString()),
				"Npolar",
				"http://api.npolar.no/dataset/?q=&format=json&variant=dcat&limit=all&filter-links.rel=data&filter-draft=no",
				"test"
		);
	}

}

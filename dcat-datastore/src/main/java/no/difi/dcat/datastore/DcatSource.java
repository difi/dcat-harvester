package no.difi.dcat.datastore;

import org.apache.jena.query.QuerySolution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DcatSource {
	
	private final String name;
	private final String url;
	private final String user;
	
	@JsonCreator
	public DcatSource(
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
	
	public static DcatSource fromQuerySolution(QuerySolution qs) {
		return new DcatSource(
				qs.get("name").asResource().getURI(), 
				qs.get("url").asResource().getURI(), 
				qs.get("user").asLiteral().getString()
				);
	}
	
	public static DcatSource getDefault() {
		return new DcatSource(
				"http://dcat.difi.no/npolar", 
				"http://api.npolar.no/dataset/?q=&format=json&variant=dcat&limit=all&filter-links.rel=data&filter-draft=no", 
				"test"
				);
	}

}

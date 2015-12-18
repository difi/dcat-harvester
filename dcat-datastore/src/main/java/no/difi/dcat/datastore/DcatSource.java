package no.difi.dcat.datastore;

import org.apache.jena.query.QuerySolution;

public class DcatSource {
	
	private String name;
	private String url;
	private String user;
	
	public DcatSource() {
		// TODO Auto-generated constructor stub
	}
	
	public DcatSource(
			String name, 
			String url, 
			String user) {
		this.name = name;
		this.url = url;
		this.user = user;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
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

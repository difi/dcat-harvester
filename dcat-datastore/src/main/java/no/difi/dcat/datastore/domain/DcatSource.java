package no.difi.dcat.datastore.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;

public class DcatSource {

	private String id;
	private String description;
	private String url;
	private String user;
	private String graph;

	private List<Harvest> harvested = new ArrayList<>();

	public DcatSource(Model dcatModel, String id) {
		Resource resource = dcatModel.getResource(id);

		this.id = id;

		url = extractExactlyOneString(resource, DifiMeta.url);
		graph = extractExactlyOneString(resource, DifiMeta.graph);
		description = extractExactlyOneString(resource, RDFS.comment);
		user = dcatModel.listResourcesWithProperty(DifiMeta.dcatSource).nextResource().listProperties(FOAF.accountName)
				.next().getString();

		StmtIterator harvestedIterator = resource.listProperties(DifiMeta.harvested);
		while (harvestedIterator.hasNext()) {
			Statement next = harvestedIterator.next();
			Resource resource1 = next.getObject().asResource();
			String s = extractExactlyOneString(resource1, DCTerms.created);
			harvested.add(new Harvest(extractExactlyOneResource(resource1, DifiMeta.status), s,
					extractExactlyOneStringOrNull(resource1, RDFS.comment)));

		}

	}

	String extractExactlyOneString(Resource resource, Property p) {
		StmtIterator stmtIterator = resource.listProperties(p);
		String ret;
		try {
			ret = stmtIterator.next().getString();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException("Tried to access " + p.getURI());
		}
		if (stmtIterator.hasNext()) {
			throw new RuntimeException("Model contains more than one string for property " + p.toString());
		}
		return ret;
	}

	String extractExactlyOneStringOrNull(Resource resource, Property p) {
		StmtIterator stmtIterator = resource.listProperties(p);
		String ret;
		try {
			ret = stmtIterator.next().getString();
		} catch (NoSuchElementException e) {
			return null;
		}
		if (stmtIterator.hasNext()) {
			throw new RuntimeException("Model contains more than one string for property " + p.toString());
		}
		return ret;
	}

	Resource extractExactlyOneResource(Resource resource, Property p) {
		StmtIterator stmtIterator = resource.listProperties(p);
		Resource ret;
		try {
			ret = stmtIterator.next().getResource();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException("Tried to access " + p.getURI());
		}
		if (stmtIterator.hasNext()) {
			throw new RuntimeException("Model contains more than one string for property " + p.toString());
		}
		return ret;
	}

	public DcatSource() {
	}

	public DcatSource(String id, String description, String url, String user) {
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
		return new DcatSource(qs.get("name").asResource().getURI(), qs.get("description").asLiteral().getString(),
				qs.get("url").asResource().getURI(), qs.get("user").asLiteral().getString());
	}

	public static DcatSource getDefault() {
		return new DcatSource(String.format("http://dcat.difi.no/%s", UUID.randomUUID().toString()), "Npolar",
				"http://api.npolar.no/dataset/?q=&format=json&variant=dcat&limit=all&filter-links.rel=data&filter-draft=no",
				"test");
	}

	public String toString() {
		return String.format("crawler_id=%1$s, crawler_name=%2$s, crawler_url=%3$s", this.getId(), this.getDescription(), this.getUrl());
	}
	
	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public List<Harvest> getHarvested() {
		return harvested;
	}

	public Optional<Harvest> getLastHarvest() {
		Optional<Harvest> harvest = harvested.stream().max(new HarvestComparator());
		return harvest;
	}

	public static class Harvest {
		private Resource status;
		private String createdDate;
		private String message;

		public Harvest(Resource status, String createdDate, String message) {
			this.status = status;
			this.createdDate = createdDate;
			this.message = message;
		}

		public String getCreatedDateFormatted() {
			Calendar cal = DatatypeConverter.parseDateTime(createdDate);
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale("NO"));

			return df.format(cal.getTime());
		}

		public Resource getStatus() {
			return status;
		}

		public String getCreatedDate() {
			return createdDate;
		}

		public String getMessage() {
			return message;
		}
	}
	
	public static class HarvestComparator implements Comparator<Harvest> {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		
		@Override
		public int compare(Harvest o1, Harvest o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}
			Calendar c1 = DatatypeConverter.parseDateTime(o1.createdDate);
			Calendar c2 = DatatypeConverter.parseDateTime(o2.createdDate);

			return c1.compareTo(c2);		
		}
	}
}

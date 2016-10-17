package no.difi.dcat.datastore.domain.dcat;

import org.apache.jena.sparql.vocabulary.FOAF;

/**
 * Representation of a FOAF:Document
 * @author Håvard Tørresen
 *
 */
public class Document{
	private String type;
	private String id;
	private String topic;
	
	public Document(){
		type = FOAF.Document.getLocalName();
	}
	
	public String getType() {
		return type;
	}
	public String getId() {
		return id;
	}
	public String getTopic() {
		return topic;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}	
}
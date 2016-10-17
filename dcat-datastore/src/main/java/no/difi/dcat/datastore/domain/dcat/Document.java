package no.difi.dcat.datastore.domain.dcat;


/**
 * Representation of a FOAF:Document
 * @author Håvard Tørresen
 *
 */
public class Document{
	private String id;
	private String topic;
	

	public String getId() {
		return id;
	}
	public String getTopic() {
		return topic;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}	
}
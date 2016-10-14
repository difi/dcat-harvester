package no.difi.dcat.datastore.domain.dcat;

import org.apache.jena.vocabulary.DCTerms;

/**
 * Represents the dct:temporal property of
 * datasets, as a dct:PeriodOfTime
 * 
 * @author Håvard Tørresen
 *
 */
public class Temporal {

	private String type;
	private String id;
	private String startDate;
	private String endDate;

	public Temporal(){
		type = DCTerms.PeriodOfTime.getLocalName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {	
		this.type = type;
	}
}
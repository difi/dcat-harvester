package no.difi.dcat.datastore.domain.dcat.builders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.dcat.Contact;
import no.difi.dcat.datastore.domain.dcat.Publisher;
import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;

public abstract class AbstractBuilder {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractBuilder.class);
	
	public static String extractAsString(Resource resource, Property property) {
		try {
			Statement statement = resource.getProperty(property);
			if (statement != null) {
				if (statement.getObject().isLiteral()) {
					return statement.getString();
				} else {
					return statement.getObject().asResource().getURI();
				}
			}
		} catch (Exception e) {
			logger.warn("Error when extracting property {} from resource {}", property, resource.getURI(), e);
		}
		return null;
	}
	
	public static List<String> extractMultipleStrings(Resource resource, Property property){
		ArrayList<String> list = new ArrayList<String>();
		
		StmtIterator iterator = resource.listProperties(property);
		while (iterator.hasNext()) {
			Statement statement = iterator.next();

			if (statement.getObject().isLiteral()) {
				list.add(statement.getString() + " literal");
			}else if (statement.getObject().isResource()) {
				list.add(statement.getObject().asResource().getURI() + " res");
			}else if (statement.getObject().isAnon()) {
				list.add(statement.getObject().asResource().getURI() + " anon");
			}				
		}
				
		return list;
	}
	
	public static Map<String, String> extractLanguageLiteral(Resource resource, Property property) {
		Map<String,String> map = new HashMap<>();
		StmtIterator iterator = resource.listProperties(property);
		while (iterator.hasNext()) {
			Statement statement = iterator.next();
			map.put(statement.getLanguage(), statement.getString());
		}
		return map;
	}
	
	public static Map<String, List<String>> extractMultipleLanguageLiterals(Resource resource, Property property) {
		Map<String, List<String>> map = new HashMap<>();
		StmtIterator iterator = resource.listProperties(property);
		while (iterator.hasNext()) {
			Statement statement = iterator.next();
			String key = statement.getLanguage();
			String value = statement.getString();
			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<>());
			}
			map.get(key).add(value);
		}
		return map;
	}
	
	public static Date extractDate(Resource resource, Property property) {
		StmtIterator iterator = resource.listProperties(property);
		while (iterator.hasNext()) {
			Statement statement = iterator.next();
			Calendar cal = null;
			try {
				cal = DatatypeConverter.parseDate(statement.getString());
				return cal.getTime();
			} catch (Exception e) {
				logger.warn("Error when extracting property {} from resource {}", property, resource.getURI(), e);
			}
		}
		return null;
	}
	
	public static Contact extractContact(Resource resource) {
		try {
			Contact contact = new Contact();
			Statement property = resource.getProperty(DCAT.contactPoint);
			Resource object = resource.getModel().getResource(property.getObject().asResource().getURI());
			contact.setId(object.getURI());
			//TODO: use correct vcard
			contact.setFullname(extractAsString(object, ResourceFactory.createProperty("http://www.w3.org/2006/vcard/ns#fn")));
			contact.setEmail(extractAsString(object, ResourceFactory.createProperty("http://www.w3.org/2006/vcard/ns#hasEmail")).replace("mailto:", ""));
			
			return contact;
		} catch (Exception e) {
			logger.warn("Error when extracting property {} from resource {}", DCTerms.publisher, resource.getURI(), e);
		}
		
		return null;
	}
	
	public static Publisher extractPublisher(Resource resource) {
		try {
			Publisher publisher = new Publisher();
			Statement property = resource.getProperty(DCTerms.publisher);
			Resource object = resource.getModel().getResource(property.getObject().asResource().getURI());
			publisher.setId(object.getURI());
			publisher.setName(extractAsString(object, FOAF.name));
			
			return publisher;
		} catch (Exception e) {
			logger.warn("Error when extracting property {} from resource {}", DCTerms.publisher, resource.getURI(), e);
		}
		
		return null;
	}
}

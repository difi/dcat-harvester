package no.difi.dcat.datastore.domain.dcat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}

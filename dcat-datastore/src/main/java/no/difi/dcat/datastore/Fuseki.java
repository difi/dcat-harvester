package no.difi.dcat.datastore;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fuseki {

	private String serviceUri;
	private final Logger logger = LoggerFactory.getLogger(Fuseki.class);
	
	public Fuseki(String serviceUri) {
		this.serviceUri = serviceUri;
	}
	
	public void update(String name, Model model) {
		logger.trace("Updating graph {} with data", name);
		DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(serviceUri);
		
		accessor.putModel(name, model);
	}
	
	public void drop(String name) {
		logger.trace("Dropping graph {}", name);
		UpdateRequest request = UpdateFactory.create() ;
		request.add("DROP GRAPH <"+name+">");
		
		UpdateProcessor processor = UpdateExecutionFactory.createRemote(request, serviceUri + "/update");
		
		processor.execute();
	}
	
	public Model construct(String query) {
		logger.trace(query);
		QueryExecution q = QueryExecutionFactory.sparqlService(serviceUri,
				query);
		Model model = q.execConstruct();
		
		return model;
	}
	
	public ResultSet select (String query) {
		logger.trace(query);
		QueryExecution q = QueryExecutionFactory.sparqlService(serviceUri,
				query);
		ResultSet results = q.execSelect();
		
		return results;
	}
	
	public static void main(String[] args) {
		String serviceURI = "http://localhost:8080/fuseki/dcat";
		Fuseki fusekiController = new Fuseki(serviceURI);
		
		Model model = FileManager.get().loadModel("data.jsonld");
		
		fusekiController.update("http://dcat.difi.no/test", model);
		
		Model model2 = fusekiController.construct("construct {?s ?p ?o} where {?s ?p ?o}");
		
		model2.getWriter("TURTLE").write(model2, System.out, null);
	}

}

package no.difi.dcat.harvester.service;

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

public class FusekiController {

	private String serviceUri;
	
	public FusekiController(String serviceUri) {
		this.serviceUri = serviceUri;
	}
	
	public void update(String name, Model model) {
		DatasetAccessor accessor;
		accessor = DatasetAccessorFactory.createHTTP(serviceUri);
		
		accessor.putModel(name, model);
	}
	
	public void drop(String name) {
		UpdateRequest request = UpdateFactory.create() ;
		request.add("DROP GRAPH <"+name+">");
		
		UpdateProcessor processor = UpdateExecutionFactory.createRemote(request, serviceUri + "/update");
		
		processor.execute();
	}
	
	public Model construct(String query) {
		QueryExecution q = QueryExecutionFactory.sparqlService(serviceUri,
				query);
		Model model = q.execConstruct();
		
		return model;
	}
	
	public ResultSet select (String query) {
		QueryExecution q = QueryExecutionFactory.sparqlService(serviceUri,
				query);
		ResultSet results = q.execSelect();
		
		return results;
	}
	
//	public static void main(String[] args) {
//		String serviceURI = "http://localhost:8080/fuseki/dcat";
//		FusekiController fusekiController = new FusekiController(serviceURI);
//		
//		Model model = FileManager.get().loadModel("data.jsonld");
//		
//		fusekiController.update("http://dcat.difi.no/test", model);
//		
//		Model model2 = fusekiController.construct("construct {?s ?p ?o} where {?s ?p ?o}");
//		
//		model2.getWriter("TURTLE").write(model2, System.out, null);
//	}

}

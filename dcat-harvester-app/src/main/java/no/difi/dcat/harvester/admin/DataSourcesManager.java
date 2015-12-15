package no.difi.dcat.harvester.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

import no.difi.dcat.harvester.crawler.CrawlerDataSource;
import no.difi.dcat.harvester.service.FusekiController;

public class DataSourcesManager {
	
	private FusekiController fusekiController;
	private Model model;
	
	public DataSourcesManager(FusekiController fusekiController) {
		this.fusekiController = fusekiController;
	}
	
	private void loadDataSourcesFromFuseki() {
		model = fusekiController.construct("CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}");
	}
	
	public void saveDataSource(CrawlerDataSource dataSource) {
		Model model = ModelFactory.createDefaultModel();
		
		Property url = model.createProperty("difi:url");
		
		model.add(model.createResource(dataSource.getName()), url, model.createResource(dataSource.getUrl()));
		
		fusekiController.drop(dataSource.getName());
		fusekiController.update(dataSource.getName(), model);
	}
	
	public List<CrawlerDataSource> getDataSources() {
		loadDataSourcesFromFuseki();
		
		List<CrawlerDataSource> dataSources = new ArrayList<>();
		Property url = model.createProperty("difi:url");
		ResIterator it = model.listResourcesWithProperty(url);
		
		while (it.hasNext()) {
			Resource r = it.next();
			CrawlerDataSource cds = new CrawlerDataSource(r.getURI(), r.getProperty(url).getObject().toString());
			dataSources.add(cds);
		}
		
		return dataSources;
	}
	
	public static void main(String[] args) {
		FusekiController fusekiController = new FusekiController("http://localhost:8080/fuseki/admin");
		DataSourcesManager manager = new DataSourcesManager(fusekiController);
		
		CrawlerDataSource dataSource = CrawlerDataSource.getDefault();
		
		manager.saveDataSource(dataSource);
		
		List<CrawlerDataSource> dataSources = manager.getDataSources();
		
		System.out.println(dataSources);
	}

}

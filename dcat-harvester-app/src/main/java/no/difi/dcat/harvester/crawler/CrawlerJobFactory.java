package no.difi.dcat.harvester.crawler;

import java.net.URL;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.LoadingCache;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.crawler.handlers.ElasticSearchResultHandler;
import no.difi.dcat.harvester.crawler.handlers.FusekiResultHandler;
import no.difi.dcat.harvester.settings.FusekiSettings;

@Component
public class CrawlerJobFactory {
	
	@Autowired
	private FusekiSettings fusekiSettings;
	
	@Autowired
	private LoadingCache<URL, String> brregCache;
	
	private AdminDataStore adminDataStore;
	private DcatDataStore dcatDataStore;
	
	private FusekiResultHandler fusekiResultHandler;
	private ElasticSearchResultHandler elasticSearchResultHandler;
	private Client client;
	
	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
		dcatDataStore = new DcatDataStore(new Fuseki(fusekiSettings.getDcatServiceUri()));
		fusekiResultHandler = new FusekiResultHandler(dcatDataStore, adminDataStore);
		
	}
	
	public CrawlerJob createCrawlerJob(DcatSource dcatSource, Client client) {
		this.setClient(client);
		elasticSearchResultHandler = new ElasticSearchResultHandler(client);
		return new CrawlerJob(dcatSource, adminDataStore, brregCache,fusekiResultHandler, elasticSearchResultHandler);
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}

package no.difi.dcat.harvester.crawler;

import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.LoadingCache;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.ElasticSearchClient;
import no.difi.dcat.harvester.crawler.handlers.ElasticSearchResultHandler;
import no.difi.dcat.harvester.crawler.handlers.FusekiResultHandler;
import no.difi.dcat.harvester.settings.FusekiSettings;

@Component
public class CrawlerJobFactory {
	
	@Autowired
	private FusekiSettings fusekiSettings;
	
	@Autowired
	private LoadingCache<URL, String> brregCache;
	
	@Autowired
	private ElasticSearchClient elasticSearchClient;
	
	private AdminDataStore adminDataStore;
	private DcatDataStore dcatDataStore;
	
	private FusekiResultHandler fusekiResultHandler;
	private ElasticSearchResultHandler elasticSearchResultHandler;
	
	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
		dcatDataStore = new DcatDataStore(new Fuseki(fusekiSettings.getDcatServiceUri()));
		fusekiResultHandler = new FusekiResultHandler(dcatDataStore, adminDataStore);
		elasticSearchResultHandler = new ElasticSearchResultHandler(elasticSearchClient);
	}
	
	public CrawlerJob createCrawlerJob(DcatSource dcatSource) {
		return new CrawlerJob(dcatSource, adminDataStore, brregCache,fusekiResultHandler, elasticSearchResultHandler);
	}

}

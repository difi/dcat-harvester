package no.difi.dcat.harvester.crawler.handlers;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Elasticsearch;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.crawler.CrawlerJob;

public class ElasticSearchResultHandlerTest {

	private static final String DCAT_INDEX = "dcat";

	private final Logger logger = LoggerFactory.getLogger(ElasticSearchResultHandler.class);

	Node node;
	Client client;
	Elasticsearch elasticsearch;

	private File homeDir = null;
	private Settings settings = null;

	@Before
	public void setUp() throws Exception {
		homeDir = new File("src/test/resources/elasticsearch");

		settings = Settings.settingsBuilder().put("path.home", homeDir.toString()).put("network.host", "0.0.0.0")
				.build();
		node = NodeBuilder.nodeBuilder().settings(settings).build();
		node.start();
		client = node.client();
		elasticsearch = new Elasticsearch();
		Assert.assertNotNull(node);
		Assert.assertFalse(node.isClosed());
		Assert.assertNotNull(client);
	}

	@After
	public void tearDown() throws Exception {
		if (client != null) {
			client.close();
		}
		if (node != null) {
			node.close();
		}
		if (homeDir != null) {
			FileUtils.forceDelete(homeDir);
		}
		node = null;
		client = null;
	}

	@Test
	public void testThatEmbeddedElasticsearchWorks() {
		ClusterHealthResponse healthResponse = null;
		try {
			healthResponse = client.admin().cluster().prepareHealth().setTimeout(new TimeValue(5000)).execute()
					.actionGet();
			logger.info("Connected to Elasticsearch: " + healthResponse.getStatus().toString());
		} catch (NoNodeAvailableException e) {
			logger.error("Failed to connect to Elasticsearch: " + e);
		}
		assertTrue(healthResponse.getStatus() != null);
	}

	@Test
	public void testCrawlingIndexesToElasticsearch() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld",
				"tester", "123456789");

		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doThrow(Exception.class).when(dcatDataStore).saveDataCatalogue(Mockito.anyObject(),
				Mockito.anyObject());

		ElasticSearchResultHandler handler = new ElasticSearchResultHandler(client);

		CrawlerJob job = new CrawlerJob(dcatSource, null, null, handler);

		job.run();
		
		assertTrue("dcat index exists", elasticsearch.indexExists(DCAT_INDEX, client));
		
		MatchAllQueryBuilder qb = null;
		qb = QueryBuilders.matchAllQuery();
		
		SearchRequestBuilder srb = client.prepareSearch(DCAT_INDEX).setQuery(QueryBuilders.matchAllQuery());
		SearchResponse sr = null;
		sr = srb.execute().actionGet();
		assertTrue("document(s) exist", sr.getHits().getTotalHits() > 0 );

	}

}
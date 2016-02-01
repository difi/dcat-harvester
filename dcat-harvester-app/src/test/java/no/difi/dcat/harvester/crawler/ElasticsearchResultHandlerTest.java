package no.difi.dcat.harvester.crawler;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Elasticsearch;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.crawler.handlers.ElasticSearchResultHandler;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
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

import java.io.File;

import static org.junit.Assert.assertTrue;

public class ElasticsearchResultHandlerTest {

    private static final String HOME_DIR = "src/test/resources/elasticsearch";
	private static final String DCAT_INDEX = "dcat";
    public static final String DATASET_TYPE = "dataset";
    public static final String DISTRIBUTION_TYPE = "distribution";

    private final Logger logger = LoggerFactory.getLogger(ElasticsearchResultHandlerTest.class);

	Node node;
	Client client;
	Elasticsearch elasticsearch;

	private File homeDir = null;
	private Settings settings = null;

	@Before
	public void setUp() throws Exception {
		homeDir = new File(HOME_DIR);

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
			Assert.assertTrue(node.isClosed());
		}
		if (homeDir != null && homeDir.exists()) {
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
		} catch (Exception e) {
			logger.error("Failed to connect to Elasticsearch: " + e);
		}
		assertTrue(healthResponse.getStatus() != null);
	}

	@Test
	public void testCrawlingIndexesToElasticsearch() {

        ClassLoader classLoader = getClass().getClassLoader();

		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", classLoader.getResource("npolar.jsonld").getFile(), "tester",
				"123456789");

		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doThrow(Exception.class).when(dcatDataStore).saveDataCatalogue(Mockito.anyObject(),
				Mockito.anyObject());

        AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);

		ElasticSearchResultHandler handler = new ElasticSearchResultHandler(client);


		CrawlerJob job = new CrawlerJob(dcatSource, adminDataStore, null, handler);

		job.run();

		assertTrue("dcat index exists", elasticsearch.indexExists(DCAT_INDEX, client));

		SearchRequestBuilder srb_distribution = client.prepareSearch(DCAT_INDEX).setTypes(DISTRIBUTION_TYPE).setQuery(QueryBuilders.matchAllQuery());
		SearchResponse sr_distribution = null;
		sr_distribution = srb_distribution.execute().actionGet();
		assertTrue("document(s) exist", sr_distribution.getHits().getTotalHits() > 0);

        SearchRequestBuilder srb_dataset = client.prepareSearch(DCAT_INDEX).setTypes(DATASET_TYPE).setQuery(QueryBuilders.matchAllQuery());
        SearchResponse sr_dataset = null;
        sr_dataset = srb_dataset .execute().actionGet();
        assertTrue("document(s) exist", sr_dataset.getHits().getTotalHits() > 0);

    }

}
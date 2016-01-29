package no.difi.dcat.datastore;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ELKTest {

	private final Logger logger = LoggerFactory.getLogger(ELKTest.class);

	Node node;
	Client client;
	Elasticsearch elasticsearch;
	Kibana kibana;

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
	public void testKibanaWorks() {
		kibana = new Kibana(client);
		assertTrue(".kibana index exists", elasticsearch.indexExists(kibana.KIBANA_INDEX, this.client));
		assertTrue("difi-* index-pattern exists", elasticsearch.documentExists(kibana.KIBANA_INDEX,
				kibana.INDEX_PATTERN_TYPE, kibana.INDEX_PATTERN_ID, this.client));
		assertTrue("crawler_operations search exists", elasticsearch.documentExists(kibana.KIBANA_INDEX,
				kibana.SEARCH_TYPE, kibana.CRAWLER_OPERATIONS_SEARCH_ID, this.client));
		assertTrue("dashboard exists", elasticsearch.documentExists(kibana.KIBANA_INDEX, kibana.DASHBOARD_TYPE,
				kibana.DASHBOARD_ID, this.client));
		assertTrue("crawler_metadata panel exists", elasticsearch.documentExists(kibana.KIBANA_INDEX,
				kibana.VISUALIZATION_TYPE, kibana.CRAWLER_METADATA_PANEL_ID, this.client));
		assertTrue("crawler_results panel exists", elasticsearch.documentExists(kibana.KIBANA_INDEX,
				kibana.VISUALIZATION_TYPE, kibana.CRAWLER_RESULTS_PANEL_ID, this.client));
		assertTrue("crawler_operations panel exists", elasticsearch.documentExists(kibana.KIBANA_INDEX,
				kibana.VISUALIZATION_TYPE, kibana.CRAWLER_OPERATIONS_PANEL_ID, this.client));
	}
}

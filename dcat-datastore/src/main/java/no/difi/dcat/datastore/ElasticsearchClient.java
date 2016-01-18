package no.difi.dcat.datastore;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchClient {

	private String serviceUri;
	private String clusterName;
	private String indexName;
	private final Logger logger = LoggerFactory.getLogger(ElasticsearchClient.class);

	// create transport client
	public Client elasticsearchClient(String host, int port, String clusterName, String nodeName) {
		// TODO: still need cluster and node names? what did I even use them for
		// in the first place?
		Client client = null;
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		} catch (UnknownHostException e) {
			logger.error(e.toString());
			// e.printStackTrace();
		}
		return client;
	}

	// create index if necessary - per datastore?
	// add data store data
	public void createElasticsearchIndex(Client client, String indexName) {
	}
	// connect to cluster - create if necessary per user?

	// update data store data
	public void updateElasticsearchIndex(Client client, String indexName) {
		client.admin().indices().flush(new FlushRequest(indexName).waitIfOngoing(true)).actionGet();
	}

	/**
	 * delete data store data TODO: decide if we want to swap in a new index vs.
	 * overwrite essentially, need to handle stuff like deletions with in the
	 * data catalogs themselves and so forth
	 */
	public void deleteElasticsearchIndex(Client client, String indexName) {
		DeleteIndexResponse delete = client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
		if (!delete.isAcknowledged()) {
			logger.error("Index wasn't deleted");
		}
	}

}

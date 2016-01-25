package no.difi.dcat.harvester;

import javax.annotation.PreDestroy;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchClient {

	private final Node node;
	private final Client client;

	public Client getClient() {
		return client;
	}

	public ElasticSearchClient() {
		node = new NodeBuilder().clusterName("elasticsearch").settings(Settings.settingsBuilder().put("http.enabled", false)).client(true).node();
		client = node.client();
	}
	
	
	
	@PreDestroy
	public void destroy() {
		if (node != null) {
			node.close();
		}
	}

}

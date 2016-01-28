package no.difi.dcat.harvester;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PreDestroy;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchClient {

	private final Client client;

	public Client getClient() {
		return client;
	}

	public ElasticSearchClient() throws UnknownHostException {
		client = TransportClient.builder().build()
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	}
	
	@PreDestroy
	public void tearDown() {
		if (client != null) {
			client.close();
		}
	}

}

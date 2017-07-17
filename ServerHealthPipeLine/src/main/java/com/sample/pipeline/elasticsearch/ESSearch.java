package com.sample.pipeline.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ESSearch {

	private Client client;
	private Node node;

	public Client initClient() throws UnknownHostException {

		Settings settings = Settings.builder().put("cluster.name", "lamscope").build();

		System.out.println("ES Client has started.");
		
		String host="127.0.0.1";
		//System.out.println(InetAddress.getByAddress(host.getBytes()));
		 
		  client = new PreBuiltTransportClient(settings)
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
		return client;


	}



	

}

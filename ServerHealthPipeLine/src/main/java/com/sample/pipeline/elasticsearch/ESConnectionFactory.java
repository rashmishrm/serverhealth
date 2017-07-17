package com.sample.pipeline.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ESConnectionFactory {

	public static Client newConnection() throws UnknownHostException {
		Settings settings = Settings.builder().put("cluster.name", "lamscope").build();

		Client connection = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
		return connection;
	}

}
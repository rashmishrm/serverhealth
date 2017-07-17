package com.sample.pipeline.elasticsearch;

import java.net.UnknownHostException;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;

public class BulkProcessorFactory {

	public static BulkProcessor create(Client connection) throws UnknownHostException {
		return BulkProcessor.builder(connection, new BulkProcessor.Listener() {

			public void beforeBulk(long executionId, BulkRequest request) {
				// TODO Auto-generated method stub

			}

			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				// TODO Auto-generated method stub

			}

			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				// TODO Auto-generated method stub

			}
			// TODO impl beforeBulk, afterBulk, afterBulk
		}).setBulkActions(2) // Max number of requests in the buffer
				.setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)) // Max
																	// document
																	// size in
																	// MB
				.setConcurrentRequests(0) // Max concurrent bulk requests
											// performed
				.build();
	}

}
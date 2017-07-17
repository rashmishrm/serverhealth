package com.sample.pipeline.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;

import com.sample.pipeline.elasticsearch.BulkProcessorFactory;
import com.sample.pipeline.elasticsearch.ESSearch;
import com.sample.pipeline.elasticsearch.ElasticSearchConfig;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class Consumer {
	private static Scanner in;

	public static void main(String[] argv) throws Exception {
		
		in = new Scanner(System.in);
		String topicName = "abc";
		String groupId = "1";

		ConsumerThread consumerRunnable = new ConsumerThread(topicName, groupId);
		consumerRunnable.start();
		String line = "";
		while (!line.equals("exit")) {
			line = in.next();
		}
		consumerRunnable.getKafkaConsumer().wakeup();
		System.out.println("Stopping consumer .....");
		consumerRunnable.join();
	}

	private static class ConsumerThread extends Thread {
		private String topicName;
		private String groupId;
		private KafkaConsumer<String, String> kafkaConsumer;

		public ConsumerThread(String topicName, String groupId) {
			this.topicName = topicName;
			this.groupId = groupId;
		}

		public void run() {
			Properties configProperties = new Properties();
			configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
			configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					"org.apache.kafka.common.serialization.ByteArrayDeserializer");
			configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					"org.apache.kafka.common.serialization.StringDeserializer");
			configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

			// Figure out where to start processing messages from
			kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
			kafkaConsumer.subscribe(Arrays.asList(topicName));
			// Start processing messages

			try {
				ESSearch es = new ESSearch();
				Client client = es.initClient();

				BulkProcessor bulkProcessor = BulkProcessorFactory.create(client);
				while (true) {
					ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
					for (ConsumerRecord<String, String> record : records) {

						String values[] = record.value().split("#");

						String json = jsonBuilder().startObject().field("id", values[0]).field("servername", values[1])
								.field("ip", values[2]).field("date", values[3]).field("status", values[4])
								.field("pingtime", values[5]).endObject().string();

						IndexRequest idx = new IndexRequest(ElasticSearchConfig.INDEX_NAME,
								ElasticSearchConfig.MAPPING_HEALTH).source(json);
						bulkProcessor.add(idx);

					}

				}
			} catch (WakeupException ex) {
				System.out.println("Exception caught " + ex.getMessage());
			} catch (Exception ex) {
				System.out.println("Exception caught " + ex.getMessage());
			} finally {
				kafkaConsumer.close();
				System.out.println("After closing KafkaConsumer");
			}
		}

		public KafkaConsumer<String, String> getKafkaConsumer() {
			return this.kafkaConsumer;
		}
	}
}

package com.sample.pipeline.elasticsearch;

public interface ElasticSearchConfig {
	String CLUSTER_NAME="elasticsearch";
	String ES_SERVER = "localhost";
	String _ALL="_all";

	String INDEX_NAME = "health_index";
	
	//mappings
	String MAPPING_HEALTH = "healthlogs";


	

}

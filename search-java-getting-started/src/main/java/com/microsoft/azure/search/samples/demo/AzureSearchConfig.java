package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.value.AutoValue;

import java.net.URL;

@AutoValue
public abstract class AzureSearchConfig {
    // These string values MUST have equivalent keys in the `azure_search_config` JSON file
    public static final String SERVICE_NAME = "ServiceName";
    public static final String API_KEY = "ApiKey";
    public static final String INDEX_NAME = "IndexName";
    public static final String INDEXER_NAME = "IndexerName";
    public static final String API_VERSION = "ApiVersion";
    public static final String DATASOURCE_NAME = "DatasourceName";
    public static final String END_POINT = "EndPoint";

    static AzureSearchConfig fromJson(String configJsonResourceName) throws java.io.IOException {
        ObjectMapper configMapper = new ObjectMapper();

        URL jsonResource = configMapper.getClass().getResource(configJsonResourceName);
        return configMapper.readValue(jsonResource, AzureSearchConfig.class);
    }

    @JsonCreator
    public static AzureSearchConfig create(
            @JsonProperty(SERVICE_NAME) String serviceName,
            @JsonProperty(API_KEY) String apiKey,
            @JsonProperty(INDEX_NAME) String indexName,
            @JsonProperty(INDEXER_NAME) String indexerName,
            @JsonProperty(API_VERSION) String apiVersion,
            @JsonProperty(DATASOURCE_NAME) String datasourceName,
            @JsonProperty(END_POINT) String endPoint) {
        return new com.microsoft.azure.search.samples.demo.AutoValue_AzureSearchConfig(serviceName, apiKey, indexName, indexerName, apiVersion, datasourceName, endPoint);
    }

    @JsonProperty(SERVICE_NAME)
    public abstract String serviceName();

    @JsonProperty(API_KEY)
    public abstract String apiKey();

    @JsonProperty(INDEX_NAME)
    public abstract String indexName();

    @JsonProperty(INDEXER_NAME)
    public abstract String indexerName();

    @JsonProperty(API_VERSION)
    public abstract String apiVersion();

    @JsonProperty(DATASOURCE_NAME)
    public abstract String datasourceName();

    @JsonProperty(END_POINT)
    public  abstract String endPoint();
}


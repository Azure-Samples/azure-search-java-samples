package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.value.AutoValue;

import java.net.URL;

@AutoValue
public abstract class AzureSearchConfig {
    public static final String SERVICE_NAME = "ServiceName";
    public static final String API_KEY = "ApiKey";

    static AzureSearchConfig fromJson(String configJsonResourceName) throws java.io.IOException {
        ObjectMapper configMapper = new ObjectMapper();

        URL jsonResource = configMapper.getClass().getResource(configJsonResourceName);
        return configMapper.readValue(jsonResource, AzureSearchConfig.class);
    }

    @JsonProperty(SERVICE_NAME)
    public abstract String serviceName();

    @JsonProperty(API_KEY)
    public abstract String apiKey();

    @JsonCreator
    public static AzureSearchConfig create(@JsonProperty(SERVICE_NAME) String serviceName, @JsonProperty(API_KEY) String apiKey) {
        return new AutoValue_AzureSearchConfig(serviceName, apiKey);
    }
}


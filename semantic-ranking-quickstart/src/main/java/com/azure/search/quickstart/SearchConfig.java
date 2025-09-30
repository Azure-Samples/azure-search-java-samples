package com.azure.search.quickstart;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SearchConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = SearchConfig.class.getClassLoader()
            .getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to load application.properties", e);
        }
    }

    public static final String SEARCH_ENDPOINT =
        properties.getProperty("azure.search.endpoint");
    public static final String INDEX_NAME =
        properties.getProperty("azure.search.index.name");
    public static final String SEMANTIC_CONFIG_NAME =
        properties.getProperty("semantic.configuration.name");

    public static final DefaultAzureCredential CREDENTIAL =
        new DefaultAzureCredentialBuilder().build();

    static {
        System.out.println("Using Azure Search endpoint: " + SEARCH_ENDPOINT);
        System.out.println("Using index name: " + INDEX_NAME + "\n");
    }
}

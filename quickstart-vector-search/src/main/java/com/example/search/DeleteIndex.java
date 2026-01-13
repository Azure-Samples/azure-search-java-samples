package com.example.search;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;

import java.io.IOException;
import java.util.Properties;

public class DeleteIndex {
    private static final String searchEndpoint;
    private static final String indexName;

    static {
        var properties = new Properties();
        try (var in = DeleteIndex.class.getClassLoader()
            .getResourceAsStream("application.properties")) {
            if (in != null) properties.load(in);
        } catch (IOException e) {
            System.out.println("Warning: could not read application.properties.");
        }

        searchEndpoint = properties.getProperty("azure.search.endpoint");
        indexName = properties.getProperty("azure.search.index-name");
    }

    public static void main(String[] args) {
        System.out.println("Using Azure Search endpoint: " + searchEndpoint);
        System.out.println("Using index name: " + indexName);

        // Create a SearchIndexClient using DefaultAzureCredential.
        SearchIndexClient searchIndexClient = new SearchIndexClientBuilder()
            .credential(new DefaultAzureCredentialBuilder().build())
            .endpoint(searchEndpoint)
            .buildClient();

        try {
            System.out.println("Deleting index...");
            searchIndexClient.deleteIndex(indexName);
            System.out.println("Index %s deleted".formatted(indexName));
        } catch (Exception ex) {
            System.err.println("Failed to delete index: " + ex);
        }

        System.exit(0);
    }
}

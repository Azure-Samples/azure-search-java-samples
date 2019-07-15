package com.microsoft.azure.search.samples.app;

import com.microsoft.azure.search.samples.service.SearchServiceClient;

import java.io.IOException;
import java.util.Properties;

public class App {

    private static Properties loadPropertiesFromResource(String resourcePath) throws IOException {
        var inputStream = App.class.getResourceAsStream(resourcePath);
        var configProperties = new Properties();
        configProperties.load(inputStream);
        return configProperties;
    }

    public static void main(String[] args) {
        try {
            var configProperties = loadPropertiesFromResource("config.properties");
            var client = new SearchServiceClient(configProperties);
            client.createIndex();
            client.createDatasource();
            client.createIndexer();
            if (client.syncIndexerData()) {
                client.performQueries();
            }else {
                System.err.print("Data indexing failed.");
            }
        } catch (Exception e) {
            System.err.println("Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.microsoft.azure.search.samples.demo;

public class App {
    public static void main(String[] args) {
        try {
            AzureSearchConfig config = AzureSearchConfig.fromJson("/azure_search_config");
            DemoOperations demoOperations = new DemoOperations(config.serviceName(), config.apiKey());
            demoOperations.createIndex();
            demoOperations.indexData();
            Thread.sleep(1000L); // wait a second to allow indexing to happen
            demoOperations.searchAllHotels();
            demoOperations.searchTwoTerms();
            demoOperations.searchWithFilter();
            demoOperations.searchTopTwo();
            demoOperations.searchOrderResults();
        } catch (Exception e) {
            System.err.println("Exception:" + e.getMessage());
        }
    }

}

package com.microsoft.azure.search.samples.demo;

public class App {
    public static void main(String[] args) {
        try {
            AzureSearchConfig config = AzureSearchConfig.fromJson("/azure_search_config");
            DemoOperations demoOperations = new DemoOperations(config);
            demoOperations.createIndex();
            Thread.sleep(1000L); // wait a second to allow indexing to happen
            demoOperations.indexData();
            Thread.sleep(1000L); // wait a second to allow indexing to happen
            demoOperations.searchSimple();
            Thread.sleep(1000L); // wait a second to allow indexing to happen
            demoOperations.searchAllFeatures();
            Thread.sleep(1000L); // wait a second to allow indexing to happen
            demoOperations.lookup();
            Thread.sleep(1000L); // wait a second to allow indexing to happen
            demoOperations.suggest();
        } catch (Exception e) {
            System.err.println("Exception:" + e.getMessage());
        }
    }
}

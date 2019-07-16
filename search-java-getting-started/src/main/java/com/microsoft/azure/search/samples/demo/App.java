package com.microsoft.azure.search.samples.demo;

public class App {
    //[Azure Search Service Name - excluding .search.windows.net];
    private static final String SERVICE_NAME = "";
    //[Enter your Azure Search Service API Key];
    private static final String API_KEY = "";

    public static void main(String[] args) {
        try {
            var config = AzureSearchConfig.fromJson("/azure_search_config");
            var demoOperations = new DemoOperations(config);

            demoOperations.createIndex();
            demoOperations.indexData();
            Thread.sleep(1000L); // wait a second to allow indexing to happen
            demoOperations.searchSimple();
            demoOperations.searchAllFeatures();
            demoOperations.lookup();
            demoOperations.suggest();
        } catch (Exception e) {
            System.err.println("Exception:" + e.getMessage());
        }
    }
}

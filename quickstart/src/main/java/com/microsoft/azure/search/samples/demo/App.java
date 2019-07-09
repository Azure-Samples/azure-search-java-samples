package com.microsoft.azure.search.samples.demo;

public class App {
    // These values are set in pom.xml
    private static final String SERVICE_NAME = System.getProperty("SERVICE_NAME");
    private static final String API_KEY = System.getProperty("API_KEY");

    public static void main(String[] args) {
        DemoOperations demoOperations = new DemoOperations(SERVICE_NAME, API_KEY);
        try {
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

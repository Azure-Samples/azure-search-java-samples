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
            var config = loadPropertiesFromResource("config.properties");
            var client = new SearchServiceClient(
                    config.getProperty("SearchServiceName"),
                    config.getProperty("SearchServiceApiKey"),
                    config.getProperty("ApiVersion"),
                    config.getProperty("IndexName")
            );

            if(client.indexExists()){ client.deleteIndex();}
            client.createIndex("index.json");
            Thread.sleep(1000L); // wait a second to create the index
            client.uploadDocuments("hotels.json");
            Thread.sleep(2000L); // wait 2 seconds for data to upload

            // Query 1
            client.logMessage("\n*QUERY 1****************************************************************");
            client.logMessage("Search for: Atlanta'");
            client.logMessage("Return: All fields'");
            client.searchPlus("Atlanta");

            // Query 2
            client.logMessage("\n*QUERY 2****************************************************************");
            client.logMessage("Search for: Atlanta");
            client.logMessage("Return: HotelName, Tags, Address");
            SearchServiceClient.SearchOptions options2 = client.new SearchOptions();
            options2.select = "HotelName,Tags,Address";
            client.searchPlus("Atlanta", options2);

            //Query 3
            client.logMessage("\n*QUERY 3****************************************************************");
            client.logMessage("Search for: wifi & restaurant");
            client.logMessage("Return: HotelName, Description, Tags");
            SearchServiceClient.SearchOptions options3 = client.new SearchOptions();
            options3.select = "HotelName,Description,Tags";
            client.searchPlus("wifi,restaurant", options3);

            // Query 4 -filtered query
            client.logMessage("\n*QUERY 4****************************************************************");
            client.logMessage("Search for: all");
            client.logMessage("Filter: Ratings greater than 4");
            client.logMessage("Return: HotelName, Rating");
            SearchServiceClient.SearchOptions options4 = client.new SearchOptions();
            options4.filter="Rating%20gt%204";
            options4.select = "HotelName,Rating";
            client.searchPlus("*",options4);

            // Query 5 - top 2 results, ordered by
            client.logMessage("\n*QUERY 5****************************************************************");
            client.logMessage("Search for: boutique");
            client.logMessage("Get: Top 2 results");
            client.logMessage("Order by: Rating in descending order");
            client.logMessage("Return: HotelId, HotelName, Category, Rating");
            SearchServiceClient.SearchOptions options5 = client.new SearchOptions();
            options5.top=2;
            options5.orderby = "Rating%20desc";
            options5.select = "HotelId,HotelName,Category,Rating";
            client.searchPlus("boutique", options5);

        } catch (Exception e) {
            System.err.println("Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }
}

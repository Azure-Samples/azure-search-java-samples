package com.microsoft.azure.search.samples.service;

import javax.json.*;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static com.microsoft.azure.search.samples.service.SearchServiceHelper.isSuccessResponse;
import static com.microsoft.azure.search.samples.service.SearchServiceHelper.logMessage;

/**
 * This class is responsible for implementing HTTP operations for creating Index, creating indexer, creating indexer datasource, ...
 */
public class SearchServiceClient {
    private final String _apiKey;
    private final Properties _properties;

    public SearchServiceClient(Properties properties) {
        _apiKey = properties.getProperty("SearchServiceApiKey");
        _properties = properties;
    }

    public boolean createIndex() throws IOException {
        logMessage("\n Creating Index...");

        URL url = com.microsoft.azure.search.samples.service.SearchServiceHelper.getCreateIndexURL(_properties);

        HttpsURLConnection connection = com.microsoft.azure.search.samples.service.SearchServiceHelper.getHttpURLConnection(url, "PUT", _apiKey);
        connection.setDoOutput(true);

        Files.copy(Paths.get("index.json"), connection.getOutputStream());

        System.out.println(connection.getResponseMessage());
        System.out.println(connection.getResponseCode());

        return isSuccessResponse(connection);
    }

    public boolean createDatasource() throws IOException {
        logMessage("\n Creating Indexer Data Source...");

        URL url = SearchServiceHelper.getCreateIndexerDatasourceURL(_properties);
        HttpsURLConnection connection = SearchServiceHelper.getHttpURLConnection(url, "PUT", _apiKey);
        connection.setDoOutput(true);

        String dataSourceRequestBody = "{ 'description' : 'Hotels Dataset','type' : '" + _properties.getProperty("DataSourceType")
                + "','credentials' : " + _properties.getProperty("DataSourceConnectionString")
                + ",'container' : { 'name' : '" + _properties.getProperty("DataSourceTable") + "' }} ";

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        outputStreamWriter.write(dataSourceRequestBody);
        outputStreamWriter.close();

        System.out.println(connection.getResponseMessage());
        System.out.println(connection.getResponseCode());

        return isSuccessResponse(connection);
    }

    public boolean createIndexer() throws IOException {
        logMessage("\n Creating Indexer...");

        URL url = SearchServiceHelper.getCreateIndexerURL(_properties);
        HttpsURLConnection connection = SearchServiceHelper.getHttpURLConnection(url, "PUT", _apiKey);
        connection.setDoOutput(true);

        String indexerRequestBody = "{ 'description' : 'Hotels data indexer', 'dataSourceName' : '" + _properties.get("DataSourceName")
                + "', 'targetIndexName' : '" + _properties.get("IndexName")
                + "' ,'parameters' : { 'maxFailedItems' : 10, 'maxFailedItemsPerBatch' : 5, 'base64EncodeKeys': false }}";

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        outputStreamWriter.write(indexerRequestBody);
        outputStreamWriter.close();

        System.out.println(connection.getResponseMessage());
        System.out.println(connection.getResponseCode());

        return isSuccessResponse(connection);
    }

    public boolean syncIndexerData() throws IOException, InterruptedException {
        // Check indexer status
        logMessage("Synchronization running...");

        boolean running = true;
        URL statusURL = SearchServiceHelper.getIndexerStatusURL(_properties);
        HttpsURLConnection connection = SearchServiceHelper.getHttpURLConnection(statusURL, "GET", _apiKey);

        while (running) {
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }

                JsonReader jsonReader = Json.createReader(connection.getInputStream());
                JsonObject responseJson = jsonReader.readObject();

                if (responseJson != null) {
                    JsonObject lastResultObject = responseJson.getJsonObject("lastResult");

                    if (lastResultObject != null) {
                        String indexerStatus = lastResultObject.getString("status");

                        if (indexerStatus.equalsIgnoreCase("inProgress")) {
                            logMessage("Synchronization running...");
                            Thread.sleep(1000);
                            statusURL = SearchServiceHelper.getIndexerStatusURL(_properties);
                            connection = SearchServiceHelper.getHttpURLConnection(statusURL, "GET", _apiKey);

                        } else {
                            running = false;
                            logMessage("Synchronized " + lastResultObject.getInt("itemsProcessed") + " rows...");
                        }
                    }
                }
            } catch (Exception e) {
                // Indexer status is slow to update initially, this loop will help us catch up.
                Thread.sleep(1000);
                statusURL = SearchServiceHelper.getIndexerStatusURL(_properties);
                connection = SearchServiceHelper.getHttpURLConnection(statusURL, "GET", _apiKey);
            }
        }

        return true;
    }

    // Queries...

    private Optional<Stream<JsonValue>> doSearch(Optional<String> maybeSearchString) {
        var searchString = maybeSearchString.orElse("*");

        try {
            URL url = SearchServiceHelper.getSearchURL(_properties, URLEncoder.encode(searchString, java.nio.charset.StandardCharsets.UTF_8.toString()));
            HttpsURLConnection connection = SearchServiceHelper.getHttpURLConnection(url, "GET", _properties.getProperty("SearchServiceApiKey"));

            JsonReader jsonReader = Json.createReader(connection.getInputStream());
            JsonObject jsonObject = jsonReader.readObject();
            JsonArray jsonArray = jsonObject.getJsonArray("value");
            jsonReader.close();

            System.out.println(connection.getResponseMessage());
            System.out.println(connection.getResponseCode());

            if (isSuccessResponse(connection)) {
                return Optional.of(jsonArray.stream());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private String jsonValueToString(JsonValue json) {
        return json.toString();
    }

    public Optional<String> performQuery(String searchString) {
        // Search, convert results to strings, combine those results
        var maybeResult = doSearch(Optional.of(searchString))
                .flatMap(jsonResults -> Optional.of(jsonResults.map(this::jsonValueToString)))
                .flatMap(stringResults -> Optional.of(stringResults.reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)))
                .flatMap(sb -> Optional.of(sb.toString()));
        return maybeResult;
    }

    private void queryAndPrint(String queryString) {
        var maybeResult = performQuery(queryString);
        maybeResult.ifPresentOrElse(s -> System.out.println(s), () -> System.out.println("No result from query"));
    }

    public void performQueries() {
        queryAndPrint("*");
    }

}

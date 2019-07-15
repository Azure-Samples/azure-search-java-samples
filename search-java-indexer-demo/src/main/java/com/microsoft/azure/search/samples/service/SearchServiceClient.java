package com.microsoft.azure.search.samples.service;

import javax.json.Json;
import javax.json.JsonValue;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static com.microsoft.azure.search.samples.service.SearchServiceHelper.logMessage;
import static com.microsoft.azure.search.samples.service.SearchServiceHelper.getIndexerStatusURL;
import static com.microsoft.azure.search.samples.service.SearchServiceHelper.getCreateIndexURL;
import static com.microsoft.azure.search.samples.service.SearchServiceHelper.getSearchURL;
import static com.microsoft.azure.search.samples.service.SearchServiceHelper.isSuccessResponse;




/**
 * This class is responsible for implementing HTTP operations for creating Index, creating indexer, creating indexer datasource, ...
 */
public class SearchServiceClient {
    private final String _apiKey;
    private final Properties _properties;
    private final static HttpClient client = HttpClient.newHttpClient();

    public SearchServiceClient(Properties properties) {
        _apiKey = properties.getProperty("SearchServiceApiKey");
        _properties = properties;
    }

    // No matter the method or contents, need to set api-key
    private static HttpRequest.Builder azureJsonRequestBuilder(URI uri, String apiKey) {
        var builder = HttpRequest.newBuilder();
        builder.uri(uri);
        builder.setHeader("content-type", "application/json");
        builder.setHeader("api-key", apiKey);
        return builder;
    }

    private HttpRequest httpRequest(URI uri, String method, String contents) {
        contents = contents == null ? "" : contents;
        var builder = azureJsonRequestBuilder(uri, this._apiKey);
        switch (method) {
            case "GET":
                builder = builder.GET();
                break;
            case "DELETE":
                builder = builder.DELETE();
                break;
            case "PUT":
                builder = builder.PUT(HttpRequest.BodyPublishers.ofString(contents));
                break;
            case "POST":
                builder = builder.POST(HttpRequest.BodyPublishers.ofString(contents));
                break;
            default:
                throw new IllegalArgumentException(String.format("Can't create request for method '%s'", method));
        }
        return builder.build();
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        logMessage(String.format("\n %sing to %s", request.method(), request.uri()));

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String loadIndexDefinitionFile(String resourcePath) throws IOException {
        var inputStream = SearchServiceClient.class.getResourceAsStream(resourcePath);
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public boolean createIndex() throws IOException, InterruptedException {
        logMessage("\n Creating index...");
        var endpoint = getCreateIndexURL(_properties);
        var indexDef = loadIndexDefinitionFile("index.json");
        var request = httpRequest(endpoint, "PUT", indexDef);
        var response = sendRequest(request);
        return isSuccessResponse(response);
    }

    public boolean createDatasource() throws IOException, InterruptedException {
        logMessage("\n Creating Indexer Data Source...");

        var endpoint = SearchServiceHelper.getCreateIndexerDatasourceURL(_properties);
        var dataSourceRequestBody = "{ 'description' : 'Hotels Dataset','type' : '" + _properties.getProperty("DataSourceType")
                + "','credentials' : " + _properties.getProperty("DataSourceConnectionString")
                + ",'container' : { 'name' : '" + _properties.getProperty("DataSourceTable") + "' }} ";
        var request = httpRequest(endpoint, "PUT", dataSourceRequestBody);
        var response = sendRequest(request);
        return isSuccessResponse(response);
    }

    public boolean createIndexer() throws IOException, InterruptedException {
        logMessage("\n Creating Indexer...");

        var endpoint = SearchServiceHelper.getCreateIndexerURL(_properties);
        var indexerRequestBody = "{ 'description' : 'Hotels data indexer', 'dataSourceName' : '" + _properties.get("DataSourceName")
                + "', 'targetIndexName' : '" + _properties.get("IndexName")
                + "' ,'parameters' : { 'maxFailedItems' : 10, 'maxFailedItemsPerBatch' : 5, 'base64EncodeKeys': false }}";

        var request = httpRequest(endpoint, "PUT", indexerRequestBody);
        var response = sendRequest(request);
        return isSuccessResponse(response);
    }

    // Returns true if response contains JSON whose last result's status reports count of synch results
    private boolean isSynchronizationStillRunning(HttpResponse<String> response) {
        var responseJson = Optional.ofNullable(Json.createReader(new StringReader(response.body())).readObject());
        if (responseJson.isEmpty()) {
            return true;
        } else {
            var responseBody = responseJson.get();
            var lastResultObject = responseBody.getJsonObject("lastResult");

            if (lastResultObject != null) {
                String indexerStatus = lastResultObject.getString("status");

                if (indexerStatus.equalsIgnoreCase("inProgress")) {
                    // Still running...
                    return true;
                } else {
                    logMessage("Synchronized " + lastResultObject.getInt("itemsProcessed") + " rows...");
                    return false;
                }
            }
            return true;
        }
    }

    // Synchronously makes status requests of indexer, repeating every second until indexer finishes. Returns true unless indexing status request results in bad status code.
    public boolean syncIndexerData() throws IOException, InterruptedException {
        // Check indexer status
        logMessage("\n Synchronization running...");

        boolean running = true;
        var statusURL = getIndexerStatusURL(_properties);
        // Loop until either request fails or synchronization completes
        while (running) {
            try {
                var request = httpRequest(statusURL, "GET", null);
                var response = sendRequest(request);
                if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }
                running = isSynchronizationStillRunning(response);
            } finally {
                if (running) {
                    logMessage("Synchronization running...");
                    // Pause a second and hit the status URL again
                    Thread.sleep(1000);
                }
            }
        }
        return true;
    }

    // Queries...

    private Optional<Stream<JsonValue>> doSearch(String searchString) {
        try {
            var uri = getSearchURL(_properties, searchString);
            var request = httpRequest(uri, "GET", null);
            var response = sendRequest(request);

            var jsonReader = Json.createReader(new StringReader(response.body()));
            var jsonArray = jsonReader.readObject().getJsonArray("value");
            jsonReader.close();

            System.out.println(String.format("Query result statuscode: %d", response.statusCode()));

            if (isSuccessResponse(response)) {
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
        searchString = searchString == null ? "*" : searchString;
        // Search, convert results to strings, combine those results
        var maybeResult = doSearch(searchString);
        return maybeResult.flatMap(jsonResults -> {
            var jsonStrings = jsonResults.map(this::jsonValueToString);
            var singleResult = jsonStrings.reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append).toString();
            return Optional.of(singleResult);
        });
    }

    private void queryAndPrint(String queryString) {
        var maybeResult = performQuery(queryString);
        maybeResult.ifPresentOrElse(s -> System.out.println(s), () -> System.out.println("No result from query"));
    }

    public void performQueries() {
        queryAndPrint("*");
        queryAndPrint("beach&$count=true");
        queryAndPrint("beach&$count=true&$select=HotelId,HotelName");
    }
}

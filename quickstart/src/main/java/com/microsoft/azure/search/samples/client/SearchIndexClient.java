package com.microsoft.azure.search.samples.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.microsoft.azure.search.samples.demo.IndexOperation;
import com.microsoft.azure.search.samples.index.IndexDefinition;
import com.microsoft.azure.search.samples.options.SearchOptions;
import com.microsoft.azure.search.samples.results.IndexBatchResult;
import com.microsoft.azure.search.samples.results.SearchResult;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class SearchIndexClient {
    private static final String API_VERSION = "2019-05-06";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new Jdk8Module());

    private final String serviceName;
    private final String indexName;
    private final String apiKey;

    public SearchIndexClient(String serviceName, String indexName, String apiKey) {
        this.serviceName = serviceName;
        this.indexName = indexName;
        this.apiKey = apiKey;
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public boolean doesIndexExist() throws IOException, InterruptedException {
        var request = httpRequest(buildIndexDefinitionUrl(), "GET").build();
        var responseCode = sendRequest(request).statusCode();
        return responseCode != HttpURLConnection.HTTP_NOT_FOUND;
    }

    /*
        public boolean doesIndexExist() throws IOException {
            HttpURLConnection connection = httpRequest(buildIndexDefinitionUrl(), "GET");
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_NOT_FOUND) {
                return false;
            }
            throwOnHttpError(connection);
            return true;
        }
    */
    public void createIndex(IndexDefinition indexDefinition) throws IOException, InterruptedException {
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).writeValue(
                connection.getOutputStream(), indexDefinition);

        var request = httpPost(buildIndexListUrl(), indexDefinitionJson).build();
        var response = sendRequest(request);
        //TODO: Check if the response code is ACCEPTED or what...
    }

    public void deleteIndexIfExists() throws IOException {
        if (doesIndexExist()) {
            HttpURLConnection connection = httpRequest(buildIndexDefinitionUrl(), "DELETE");
            throwOnHttpError(connection);
        }
    }

    public IndexBatchResult indexBatch(final List<IndexOperation> operations) throws IOException {
        return withHttpRetry(() -> {
            HttpURLConnection connection = httpRequest(buildIndexingUrl(), "POST");
            connection.setDoOutput(true);
            OBJECT_MAPPER.writeValue(connection.getOutputStream(), new IndexBatch(operations));
            throwOnHttpError(connection);
            return OBJECT_MAPPER.readValue(connection.getInputStream(), IndexBatchResult.class);
        });
    }

    public SearchResult search(final String search, final SearchOptions options) throws IOException {
        return withHttpRetry(() -> {
            HttpURLConnection connection = httpRequest(buildSearchUrl(search, options), "GET");
            throwOnHttpError(connection);
            return OBJECT_MAPPER.readValue(connection.getInputStream(), SearchResult.class);
        });
    }

    /*
    private HttpURLConnection httpRequest(String url, String method) throws IOException {
        var connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("api-key", this.apiKey);
        return connection;
    }
     */

    private static HttpRequest.Builder azureJsonRequestBuilder(String url, String apiKey) {
        var builder = HttpRequest.newBuilder();
        builder.uri(URI.create(url));
        builder.setHeader("content-type", "application/json");
        builder.setHeader("api-key", apiKey);
        return builder;
    }

    private HttpRequest.Builder httpRequest(String url, String method) {
        var builder = azureJsonRequestBuilder(url, this.apiKey);
        switch (method) {
            case "GET":
                builder = builder.GET();
                break;
            case "DELETE":
                builder = builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException(String.format("Can't create request for method '%s'", method));
        }
        return builder;
    }

    private HttpRequest.Builder httpPost(String url, String contents) {
        var builder = azureJsonRequestBuilder(url, this.apiKey);
        builder.POST(HttpRequest.BodyPublishers.ofString(contents));
        return builder;
    }

    private void throwOnHttpError(HttpURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
            String message = String.format("HTTP error. Code: %s. Message: %s", code, connection.getResponseMessage());
            if (code == HttpURLConnection.HTTP_UNAVAILABLE) {
                // this typically means the server is asking for back off + retry
                throw new HttpRetryException(message, code);
            } else {
                throw new ConnectException(message);
            }
        }
    }

    private String buildIndexListUrl() {
        return String.format("https://%s.search.windows.net/indexes?api-version=%s", this.serviceName, API_VERSION);
    }

    private String buildIndexDefinitionUrl() {
        return String.format("https://%s.search.windows.net/indexes/%s?api-version=%s", this.serviceName,
                this.indexName, API_VERSION);
    }

    private String buildIndexingUrl() {
        return String.format("https://%s.search.windows.net/indexes/%s/docs/index?api-version=%s", this.serviceName,
                this.indexName, API_VERSION);
    }


    private String buildSearchUrl(String search, SearchOptions options) throws IOException {
        StringBuilder url = new StringBuilder(
                String.format("https://%s.search.windows.net/indexes/%s/docs?api-version=%s&search=%s&$count=%s",
                        this.serviceName, this.indexName, API_VERSION, URLEncoder.encode(search, "UTF-8"),
                        options.includeCount().orElse(false)));
        if (options.filter().isPresent()) {
            url.append("&$filter=").append(URLEncoder.encode(options.filter().get(), "UTF-8"));
        }
        if (options.orderBy().isPresent()) {
            url.append("&$orderby=").append(URLEncoder.encode(options.orderBy().get(), "UTF-8"));
        }
        if (options.select().isPresent()) {
            url.append("&$select=").append(URLEncoder.encode(options.select().get(), "UTF-8"));
        }
        if (options.top().isPresent()) {
            url.append("&$top=").append(options.top().get());
        }

        return url.toString();
    }


    private static <T> T withHttpRetry(RetriableHttpOperation<T> r) throws IOException {
        final int maxRetries = 3;
        final int delayInMilliSec = 30000;
        int count = 0;
        T result;
        while (true) {
            try {
                result = r.run();
                break;
            } catch (HttpRetryException e) {
                if (++count == maxRetries) {
                    throw e;
                }
            }
            try {
                Thread.sleep(delayInMilliSec * count);
            } catch (InterruptedException e) {
                throw new IOException("Interrupted during HTTP retry", e);
            }
        }

        return result;
    }

    private static class IndexBatch {
        private List<IndexOperation> value;

        IndexBatch(List<IndexOperation> operations) {
            value = new ArrayList<>(operations);
        }
    }

    private interface RetriableHttpOperation<T> {
        T run() throws IOException;
    }
}

package com.microsoft.azure.search.samples.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchIndexClient {
    private static final String API_VERSION = "2019-05-06";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String serviceName;
    private final String indexName;
    private final String apiKey;

    public SearchIndexClient(String serviceName, String indexName, String apiKey) {
        this.serviceName = serviceName;
        this.indexName = indexName;
        this.apiKey = apiKey;
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpRequest.Builder azureJsonRequestBuilder(String url, String apiKey) {
        final var builder = HttpRequest.newBuilder();
        builder.uri(URI.create(url));
        builder.setHeader("content-type", "application/json");
        builder.setHeader("api-key", apiKey);
        return builder;
    }

    private static <T> T withHttpRetry(RetriableHttpOperation<T> r) throws IOException {
        final var maxRetries = 3;
        final var delayInMilliSec = 30000;
        var count = 0;
        T result;
        try {
            while (true) {
                try {
                    result = r.run();
                    break;
                } catch (HttpRetryException e) {
                    if (++count == maxRetries) {
                        throw e;
                    }
                }
                Thread.sleep(delayInMilliSec * count);
            }
        } catch (InterruptedException e) {
            throw new IOException("Interrupted during HTTP operation", e);
        }
        return result;
    }

    public boolean doesIndexExist() throws IOException, InterruptedException {
        final var request = httpRequest(buildIndexDefinitionUrl(), "GET").build();
        final var responseCode = sendRequest(request).statusCode();
        return responseCode != HttpURLConnection.HTTP_NOT_FOUND;
    }

    public void createIndex(IndexDefinition indexDefinition) throws IOException, InterruptedException {
        final var indexDefinitionJson = OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).writeValueAsString(indexDefinition);

        final var request = httpPost(buildIndexListUrl(), indexDefinitionJson).build();
        final var response = sendRequest(request);
        throwOnHttpError(response);
    }

    public void deleteIndexIfExists() throws IOException, InterruptedException {
        if (doesIndexExist()) {
            final var request = httpRequest(buildIndexDefinitionUrl(), "DELETE").build();
            final var response = sendRequest(request);
            throwOnHttpError(response);
        }
    }

    public IndexBatchResult indexBatch(final List<IndexOperation> operations) throws IOException {
        final var endpoint = buildIndexingUrl();
        final var requestBody = OBJECT_MAPPER.writeValueAsString(new IndexBatch(operations));
        return withHttpRetry(() -> {
            final var request = httpPost(endpoint, requestBody).build();
            final var response = sendRequest(request);
            throwOnHttpError(response);
            return OBJECT_MAPPER.readValue(response.body(), IndexBatchResult.class);
        });
    }

    public SearchResult search(final String search, final SearchOptions options) throws IOException {
        final var endpoint = buildSearchUrl(search, options);
        return withHttpRetry(() -> {
            final var request = httpRequest(endpoint, "GET").build();
            final var response = sendRequest(request);
            throwOnHttpError(response);
            return OBJECT_MAPPER.readValue(response.body(), SearchResult.class);
        });
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
        final var builder = azureJsonRequestBuilder(url, this.apiKey);
        builder.POST(HttpRequest.BodyPublishers.ofString(contents));
        return builder;
    }

    private void throwOnHttpError(HttpResponse<String> response) throws IOException {
        var code = response.statusCode();
        if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
            String message = String.format("HTTP error. Code: %s. Message: %s", code, response.body());
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
        final var url = new StringBuilder(
                String.format("https://%s.search.windows.net/indexes/%s/docs?api-version=%s&search=%s&$count=%s",
                        this.serviceName, this.indexName, API_VERSION, URLEncoder.encode(search, StandardCharsets.UTF_8),
                        options.includeCount().orElse(false)));
        if (options.filter().isPresent()) {
            url.append("&$filter=").append(URLEncoder.encode(options.filter().get(), StandardCharsets.UTF_8));
        }
        if (options.orderBy().isPresent()) {
            url.append("&$orderby=").append(URLEncoder.encode(options.orderBy().get(), StandardCharsets.UTF_8));
        }
        if (options.select().isPresent()) {
            url.append("&$select=").append(URLEncoder.encode(options.select().get(), StandardCharsets.UTF_8));
        }
        if (options.top().isPresent()) {
            url.append("&$top=").append(options.top().get());
        }

        return url.toString();
    }

    private interface RetriableHttpOperation<T> {
        T run() throws IOException, InterruptedException;
    }

    private static class IndexBatch {
        private List<IndexOperation> value;

        IndexBatch(List<IndexOperation> operations) {
            value = new ArrayList<>(operations);
        }
    }
}

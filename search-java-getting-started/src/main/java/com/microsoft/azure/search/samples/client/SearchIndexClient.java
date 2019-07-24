package com.microsoft.azure.search.samples.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.microsoft.azure.search.samples.demo.AzureSearchConfig;
import com.microsoft.azure.search.samples.demo.IndexOperation;
import com.microsoft.azure.search.samples.index.IndexDefinition;
import com.microsoft.azure.search.samples.options.SearchOptions;
import com.microsoft.azure.search.samples.options.SuggestOptions;
import com.microsoft.azure.search.samples.results.IndexBatchResult;
import com.microsoft.azure.search.samples.results.SearchResult;
import com.microsoft.azure.search.samples.results.SuggestResult;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.microsoft.azure.search.samples.client.SearchServiceHelper.*;

public class SearchIndexClient {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new Jdk8Module());
    private static final HttpClient client = HttpClient.newHttpClient();
    private final AzureSearchConfig config;

    public SearchIndexClient(AzureSearchConfig config) {
        this.config = config;
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        logMessage(String.format("\n %sing to %s", request.method(), request.uri()));

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static <T> T withHttpRetry(RetriableHttpOperation<T> r) throws IOException {
        final int maxRetries = 3;
        final int delayInMilliSec = 30000;
        int count = 0;
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
            throw new IOException("Interrupted during HTTP retry", e);
        }

        return result;
    }

    private HttpRequest httpRequest(URI endpoint, String method, String bodyContents) {
        return SearchServiceHelper.httpRequest(endpoint, config.apiKey(), method, bodyContents);
    }

    public boolean doesIndexExist() throws IOException, InterruptedException {
        final var endpoint = getIndexUrl(config);
        final var request = httpRequest(endpoint, "GET", null);
        final var response = sendRequest(request);
        return isSuccessResponse(response);
    }

    public void createIndex(IndexDefinition indexDefinition) throws IOException, InterruptedException {
        final var endpoint = getIndexUrl(config);
        final var body = OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).writeValueAsString(indexDefinition);
        final var request = httpRequest(endpoint, "PUT", body);
        final var response = sendRequest(request);
        throwOnHttpError(response);
    }

    public void deleteIndexIfExists() throws IOException, InterruptedException {
        logMessage("Checking if index exists");
        if (doesIndexExist()) {
            logMessage("Deleting existing index");
            final var endpoint = getIndexUrl(config);
            final var request = httpRequest(endpoint, "DELETE", null);
            final var response = sendRequest(request);
            throwOnHttpError(response);
        } else {
            logMessage("Index does not exist yet");
        }
    }

    public IndexBatchResult indexBatch(final List<IndexOperation> operations) throws IOException {
        final var endpoint = getIndexingUrl(config);
        final var body = OBJECT_MAPPER.writeValueAsString(new IndexBatch(operations));
        return withHttpRetry(() -> {
            final var request = httpRequest(endpoint, "POST", body);
            final var response = sendRequest(request);
            throwOnHttpError(response);
            return OBJECT_MAPPER.readValue(response.body(), IndexBatchResult.class);
        });
    }

    public SearchResult search(final String search, final SearchOptions options) throws IOException {
        final var endpoint = SearchServiceHelper.buildSearchUrl(config, search, options);
        return withHttpRetry(() -> {
            final var request = httpRequest(endpoint, "GET", null);
            final var response = sendRequest(request);
            throwOnHttpError(response);
            return OBJECT_MAPPER.readValue(response.body(), SearchResult.class);
        });
    }

    public SuggestResult suggest(final String search, final String suggesterName, final SuggestOptions options)
            throws IOException {
        final var endpoint = SearchServiceHelper.buildIndexSuggestUrl(config, search, suggesterName, options);
        return withHttpRetry(() -> {
            final var request = httpRequest(endpoint, "GET", null);
            final var response = sendRequest(request);
            throwOnHttpError(response);
            return OBJECT_MAPPER.readValue(response.body(), SuggestResult.class);
        });
    }

    public Map<String, Object> lookup(final String key) throws IOException {
        final var endpoint = SearchServiceHelper.getIndexLookupUrl(config, key);
        return withHttpRetry(() -> {
            final var request = httpRequest(endpoint, "GET", null);
            final var response = sendRequest(request);
            throwOnHttpError(response);
            Map<String, Object> document = OBJECT_MAPPER.readValue(response.body(), new TypeReference<Map<String, Object>>() {
            });
            document.remove("@odata.context");
            return document;
        });
    }

    private void throwOnHttpError(HttpResponse<String> response) throws IOException {
        int code = response.statusCode();
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

    private interface RetriableHttpOperation<T> {
        T run() throws IOException, InterruptedException;
    }

    private static class IndexBatch {
        private final List<IndexOperation> value;

        IndexBatch(List<IndexOperation> operations) {
            value = new ArrayList<>(operations);
        }
    }
}

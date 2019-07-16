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
import java.net.*;
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
    private AzureSearchConfig config;

    public SearchIndexClient(AzureSearchConfig config) {
        this.config = config;
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        logMessage(String.format("\n %sing to %s", request.method(), request.uri()));

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String escapePathSegment(String segment) throws IOException {
        // URLEncoder.encode() is the wrong thing to use in this case, work-around with URI below
        try {
            URI uri = new URI("https", "temporary-service-name.temporary-domain.temporary-tld", "/" + segment, "");
            return uri.getPath().substring(1);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid segment content");
        }
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
        final var request = httpRequest(endpoint, "POST", body);
        final var response = sendRequest(request);
        throwOnHttpError(response);
    }

    public void deleteIndexIfExists() throws IOException, InterruptedException {
        if (doesIndexExist()) {
            final var endpoint = getIndexUrl(config);
            final var request = httpRequest(endpoint, "DELETE", null);
            final var response = sendRequest(request);
            throwOnHttpError(response);
        }
    }

    public IndexBatchResult indexBatch(final List<IndexOperation> operations) throws IOException, InterruptedException {
        final var endpoint = getIndexUrl(config);
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

    private String indexesUrlOld() {
        return String.format("https://%s.search.windows.net/indexes?api-version=%s", config.serviceName(), config.apiVersion());
    }

    private String indexUrlOld() {
        return String.format("https://%s.search.windows.net/indexes/%s?api-version=%s", config.serviceName(),
                config.indexName(), config.apiVersion());
    }

    private String buildIndexingUrl() {
        return String.format("https://%s.search.windows.net/indexes/%s/docs/index?api-version=%s", config.serviceName(),
                config.indexName(), config.apiVersion());
    }

    private String buildIndexLookupUrl(String key) throws IOException {
        return String.format("https://%s.search.windows.net/indexes/%s/docs('%s')?api-version=%s", config.serviceName(),
                config.indexName(), escapePathSegment(key), config.apiVersion());
    }

    private String buildSearchUrl(String search, SearchOptions options) throws IOException {
        StringBuilder url = new StringBuilder(
                String.format("https://%s.search.windows.net/indexes/%s/docs?api-version=%s&search=%s&$count=%s",
                        config.serviceName(), config.indexName(), config.apiVersion(), URLEncoder.encode(search, "UTF-8"),
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
        if (options.searchFields().isPresent()) {
            url.append("&searchFields=").append(URLEncoder.encode(options.searchFields().get(), "UTF-8"));
        }
        if (!options.facets().isEmpty()) {
            for (String f : options.facets()) {
                url.append("&facet=").append(URLEncoder.encode(f, "UTF-8"));
            }
        }
        if (options.highlight().isPresent()) {
            url.append("&highlight=").append(URLEncoder.encode(options.highlight().get(), "UTF-8"));
        }
        if (options.highlightPreTag().isPresent()) {
            url.append("&highlightPreTag=").append(URLEncoder.encode(options.highlightPreTag().get(), "UTF-8"));
        }
        if (options.highlightPostTag().isPresent()) {
            url.append("&highlightPostTag=").append(URLEncoder.encode(options.highlightPostTag().get(), "UTF-8"));
        }
        if (options.scoringProfile().isPresent()) {
            url.append("&scoringProfile=").append(URLEncoder.encode(options.scoringProfile().get(), "UTF-8"));
        }
        if (!options.scoringParameters().isEmpty()) {
            for (String p : options.scoringParameters()) {
                url.append("&scoringParameter=").append(URLEncoder.encode(p, "UTF-8"));
            }
        }
        if (options.top().isPresent()) {
            url.append("&$top=").append(options.top().get());
        }
        if (options.skip().isPresent()) {
            url.append("&$skip=").append(options.skip().get());
        }
        if (options.requireAllTerms()) {
            url.append("&searchMode=all");
        }
        if (options.minimumCoverage().isPresent()) {
            url.append("&minimumCoverage=").append(options.minimumCoverage().get());
        }
        return url.toString();
    }

    private String buildIndexSuggestUrl(String search, String suggesterName, SuggestOptions options)
            throws IOException {
        StringBuilder url = new StringBuilder(String.format(
                "https://%s.search.windows.net/indexes/%s/docs/suggest?api-version=%s&search=%s&suggesterName=%s",
                config.serviceName(), config.indexName(), config.apiVersion(), URLEncoder.encode(search, "UTF-8"), suggesterName));
        if (options.filter().isPresent()) {
            url.append("&$filter=").append(URLEncoder.encode(options.filter().get(), "UTF-8"));
        }
        if (options.orderby().isPresent()) {
            url.append("&$orderby=").append(URLEncoder.encode(options.orderby().get(), "UTF-8"));
        }
        if (options.select().isPresent()) {
            url.append("&$select=").append(URLEncoder.encode(options.select().get(), "UTF-8"));
        }
        if (options.searchFields().isPresent()) {
            url.append("&searchFields=").append(URLEncoder.encode(options.searchFields().get(), "UTF-8"));
        }
        if (options.highlightPreTag().isPresent()) {
            url.append("&highlightPreTag=").append(URLEncoder.encode(options.highlightPreTag().get(), "UTF-8"));
        }
        if (options.highlightPostTag().isPresent()) {
            url.append("&highlightPostTag=").append(URLEncoder.encode(options.highlightPostTag().get(), "UTF-8"));
        }
        if (options.fuzzy()) {
            url.append("&fuzzy=true");
        }
        if (options.top().isPresent()) {
            url.append("&$top=").append(options.top().get());
        }
        if (options.minimumCoverage().isPresent()) {
            url.append("&minimumCoverage=").append(options.minimumCoverage().get());
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

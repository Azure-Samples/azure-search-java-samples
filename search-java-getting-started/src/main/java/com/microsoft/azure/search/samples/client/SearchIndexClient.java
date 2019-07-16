package com.microsoft.azure.search.samples.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchIndexClient {
    private static final String API_VERSION = "2017-11-11-Preview";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new Jdk8Module());

    private final String serviceName;
    private final String indexName;
    private final String apiKey;

    public SearchIndexClient(String serviceName, String indexName, String apiKey) {
        this.serviceName = serviceName;
        this.indexName = indexName;
        this.apiKey = apiKey;
    }

    public boolean doesIndexExist() throws IOException {
        HttpURLConnection connection = httpRequest(buildIndexDefinitionUrl(), "GET");
        int response = connection.getResponseCode();
        if (response == HttpURLConnection.HTTP_NOT_FOUND) {
            return false;
        }
        throwOnHttpError(connection);
        return true;
    }

    public void createIndex(IndexDefinition indexDefinition) throws IOException {
        HttpURLConnection connection = httpRequest(buildIndexListUrl(), "POST");
        connection.setDoOutput(true);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).writeValue(
                connection.getOutputStream(), indexDefinition);
        throwOnHttpError(connection);
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

    public SuggestResult suggest(final String search, final String suggesterName, final SuggestOptions options)
            throws IOException {
        return withHttpRetry(() -> {
            HttpURLConnection connection = httpRequest(buildIndexSuggestUrl(search, suggesterName, options), "GET");
            throwOnHttpError(connection);
            return OBJECT_MAPPER.readValue(connection.getInputStream(), SuggestResult.class);
        });
    }

    public Map<String, Object> lookup(final String key) throws IOException {
        return withHttpRetry(() -> {
            HttpURLConnection connection = httpRequest(buildIndexLookupUrl(key), "GET");
            throwOnHttpError(connection);
            Map<String, Object> document = OBJECT_MAPPER.readValue(connection.getInputStream(),
                                                                   new TypeReference<Map<String, Object>>() {});
            document.remove("@odata.context");
            return document;
        });
    }

    private HttpURLConnection httpRequest(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("api-key", this.apiKey);
        return connection;
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

    private String buildIndexLookupUrl(String key) throws IOException {
        return String.format("https://%s.search.windows.net/indexes/%s/docs('%s')?api-version=%s", this.serviceName,
                             this.indexName, escapePathSegment(key), API_VERSION);
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
                this.serviceName, this.indexName, API_VERSION, URLEncoder.encode(search, "UTF-8"), suggesterName));
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

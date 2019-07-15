package com.microsoft.azure.search.samples.service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.Formatter;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Helper class that contains static helper methods.
 * Also it contains static/constant Strings like Index Name, Indexer Name, Data source Name, ...
 */
public class SearchServiceHelper {
    private static final String _searchURL = "https://%s.search.windows.net/indexes/%s/docs?api-version=%s&search=%s&searchMode=all";
    private static final String _createIndexURL = "https://%s.search.windows.net/indexes/%s?api-version=%s";
    private static final String _createIndexerDatasourceURL = "https://%s.search.windows.net/datasources/%s?api-version=%s";
    private static final String _createIndexerURL = "https://%s.search.windows.net/indexers/%s?api-version=%s";
    private static final String _runIndexerURL = "https://%s.search.windows.net/indexers/%s/run?api-version=%s";
    private static final String _getIndexerStatusURL = "https://%s.search.windows.net/indexers/%s/status?api-version=%s";

    private static URI buildURI(Consumer<Formatter> fmtFn)
    {
        Formatter strFormatter = new Formatter();
        fmtFn.accept(strFormatter);
        String url = strFormatter.out().toString();
        strFormatter.close();
        return URI.create(url);
    }

    public static URI getSearchURL(Properties properties, String query) {
        return buildURI(strFormatter -> strFormatter.format(_searchURL, properties.get("SearchServiceName"), properties.get("IndexName"), properties.get("ApiVersion"), query));
    }

    public static URI getCreateIndexURL(Properties properties) {
        return buildURI(strFormatter -> strFormatter.format(_createIndexURL, properties.get("SearchServiceName"), properties.get("IndexName"), properties.get("ApiVersion")));
    }

    public static URI getCreateIndexerURL(Properties properties) {
        return buildURI(strFormatter -> strFormatter.format(_createIndexerURL, properties.get("SearchServiceName"), properties.get("IndexerName"), properties.get("ApiVersion")));
    }

    public static URI getCreateIndexerDatasourceURL(Properties properties) {
        return buildURI(strFormatter -> strFormatter.format(_createIndexerDatasourceURL, properties.get("SearchServiceName"), properties.get("DataSourceName"), properties.get("ApiVersion")));
    }

    public static URI getRunIndexerURL(Properties properties) {
        return buildURI(strFormatter -> strFormatter.format(_runIndexerURL, properties.get("SearchServiceName"), properties.get("IndexerName"), properties.get("ApiVersion")));
    }

    public static URI getIndexerStatusURL(Properties properties) {
        return buildURI(strFormatter -> strFormatter.format(_getIndexerStatusURL, properties.get("SearchServiceName"), properties.get("IndexerName"), properties.get("ApiVersion")));
    }

    public static void logMessage(String message) {
        System.out.println(message);
    }

    public static boolean isSuccessResponse(HttpResponse<String> response) {
        try {
            int responseCode = response.statusCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED
                    || responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpsURLConnection.HTTP_CREATED) {
                return true;
            }

            // We got an error
            var msg = response.body();
            if (msg != null) {
                logMessage(String.format("\n ERROR: %s", msg));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}

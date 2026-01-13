package com.example.search;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;
import com.azure.core.util.Context;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.models.VectorizedQuery;
import com.azure.search.documents.models.VectorSearchOptions;
import com.azure.search.documents.models.VectorFilterMode;

import java.io.IOException;
import java.util.Properties;

public class SearchSingle {
    private static final String searchEndpoint;
    private static final String indexName;

    static {
        var properties = new Properties();
        try (var in = SearchSingle.class.getClassLoader()
            .getResourceAsStream("application.properties")) {
            if (in != null) properties.load(in);
        } catch (IOException e) {
            System.out.println("Warning: could not read application.properties.");
        }

        searchEndpoint = properties.getProperty("azure.search.endpoint");
        indexName = properties.getProperty("azure.search.index-name");
    }

    public static void main(String[] args) {
        System.out.println("Using Azure Search endpoint: " + searchEndpoint);
        System.out.println("Using index name: " + indexName);

        // Create a SearchClient using DefaultAzureCredential.
        var searchClient = new SearchClientBuilder()
            .credential(new DefaultAzureCredentialBuilder().build())
            .endpoint(searchEndpoint)
            .indexName(indexName)
            .buildClient();

        try {
            var vectorQuery = new VectorizedQuery(QueryVector.getVectorList())
                .setKNearestNeighborsCount(5)
                .setFields("DescriptionVector")
                .setExhaustive(true);

            // Create vector search options with the vector query and filter.
            // Use POST_FILTER to apply filter after vector similarity is calculated.
            var vectorSearchOptions = new VectorSearchOptions()
                .setQueries(vectorQuery)
                .setFilterMode(VectorFilterMode.POST_FILTER);

            var searchOptions = new SearchOptions()
                .setTop(7)
                .setIncludeTotalCount(true)
                .setSelect("HotelId", "HotelName", "Description", "Category", "Tags")
                .setVectorSearchOptions(vectorSearchOptions);

            var results = searchClient.search("*", searchOptions, Context.NONE);

            System.out.println();
            Long count = results.getTotalCount();
            System.out.println("Single Vector search found %s"
                .formatted(count == null ? 0 : count));

            for (SearchResult result : results) {
                // Log each result.
                SearchDocument document = result.getDocument(SearchDocument.class);
                Object tags = document.get("Tags");
                var tagsString = (tags == null ? "N/A" : tags.toString());
                System.out.println(
                    "- HotelId: %s, HotelName: %s, Tags: %s, Score: %s".formatted(
                        document.get("HotelId"), document.get("HotelName"),
                        tagsString, result.getScore()));
            }
        } catch (Exception ex) {
            System.err.println("Search failed: " + ex);
        }

        System.exit(0);
    }
}

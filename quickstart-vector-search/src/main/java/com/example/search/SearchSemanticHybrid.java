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
import com.azure.search.documents.models.QueryType;
import com.azure.search.documents.models.SemanticSearchOptions;

import java.io.IOException;
import java.util.Properties;

public class SearchSemanticHybrid {
    private static final String searchEndpoint;
    private static final String indexName;

    static {
        var properties = new Properties();
        try (var in = SearchSemanticHybrid.class.getClassLoader()
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

            SemanticSearchOptions semanticSearchOptions = new SemanticSearchOptions()
                .setSemanticConfigurationName("semantic-config");

            var searchOptions = new SearchOptions()
                .setTop(5)
                .setIncludeTotalCount(true)
                .setSelect("HotelId", "HotelName", "Category", "Description")
                .setQueryType(QueryType.SEMANTIC)
                .setSemanticSearchOptions(semanticSearchOptions)
                .setVectorSearchOptions(vectorSearchOptions);

            var results = searchClient.search(
                "historic hotel walk to restaurants and shopping",
                searchOptions, Context.NONE);

            System.out.println();
            Long count = results.getTotalCount();
            System.out.println("Semantic hybrid search found %s then limited to top %s"
                .formatted(count == null ? 0 : count,
                    searchOptions.getTop() == null ? 0 : searchOptions.getTop()));

            for (SearchResult result : results) {
                // Log each result.
                SearchDocument document = result.getDocument(SearchDocument.class);
                var semanticResult = result.getSemanticSearch();
                Double rerankerScore = semanticResult == null ?
                    null : semanticResult.getRerankerScore();
                Object desc = document.get("Description");
                Object category = document.get("Category");

                System.out.println(
                    "- Score: %s\n".formatted(result.getScore()) +
                    "  Re-ranker Score: %s\n".formatted(rerankerScore == null ? "N/A" : rerankerScore) +
                    "  HotelId: %s\n".formatted(document.get("HotelId")) +
                    "  HotelName: %s\n".formatted(document.get("HotelName")) +
                    "  Description: %s\n".formatted(desc == null ? "N/A" : desc) +
                    "  Category: %s\n".formatted(category == null ? "N/A" : category)
                );
            }
        } catch (Exception ex) {
            System.err.println("Semantic hybrid search failed: " + ex);
        }

        System.exit(0);
    }
}

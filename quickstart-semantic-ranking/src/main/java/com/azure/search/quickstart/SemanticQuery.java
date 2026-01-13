package com.azure.search.quickstart;

import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.models.QueryType;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;
import com.azure.search.documents.models.SemanticSearchOptions;
import com.azure.search.documents.util.SearchPagedIterable;

public class SemanticQuery {
    public static void main(String[] args) {
        var searchClient = new SearchClientBuilder()
            .endpoint(SearchConfig.SEARCH_ENDPOINT)
            .indexName(SearchConfig.INDEX_NAME)
            .credential(SearchConfig.CREDENTIAL)
            .buildClient();

        var searchOptions = new SearchOptions()
            .setQueryType(QueryType.SEMANTIC)
            .setSemanticSearchOptions(new SemanticSearchOptions()
                .setSemanticConfigurationName(SearchConfig.SEMANTIC_CONFIG_NAME))
            .setSelect("HotelId", "HotelName", "Description");

        SearchPagedIterable results = searchClient.search(
            "walking distance to live music", searchOptions, null);

        int rowNumber = 1;
        for (SearchResult result : results) {
            var document = result.getDocument(SearchDocument.class);
            double rerankerScore = result.getSemanticSearch().getRerankerScore();

            System.out.printf("Search result #%d:%n", rowNumber++);
            System.out.printf("  Re-ranker Score: %.2f%n", rerankerScore);
            System.out.printf("  HotelId: %s%n", document.get("HotelId"));
            System.out.printf("  HotelName: %s%n", document.get("HotelName"));
            System.out.printf("  Description: %s%n%n",
                document.get("Description") != null ?
                    document.get("Description") : "N/A");
        }

        System.exit(0);
    }
}

package com.azure.search.quickstart;

import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.models.QueryCaption;
import com.azure.search.documents.models.QueryCaptionResult;
import com.azure.search.documents.models.QueryCaptionType;
import com.azure.search.documents.models.QueryType;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;
import com.azure.search.documents.models.SemanticSearchOptions;
import com.azure.search.documents.util.SearchPagedIterable;
import java.util.List;

public class SemanticQueryWithCaptions {
    public static void main(String[] args) {
        var searchClient = new SearchClientBuilder()
            .endpoint(SearchConfig.SEARCH_ENDPOINT)
            .indexName(SearchConfig.INDEX_NAME)
            .credential(SearchConfig.CREDENTIAL)
            .buildClient();

        System.out.println("Using semantic configuration: " +
            SearchConfig.SEMANTIC_CONFIG_NAME);
        System.out.println("Search query: walking distance to live music");

        var searchOptions = new SearchOptions()
            .setQueryType(QueryType.SEMANTIC)
            .setSemanticSearchOptions(new SemanticSearchOptions()
                .setSemanticConfigurationName(SearchConfig.SEMANTIC_CONFIG_NAME)
                .setQueryCaption(new QueryCaption(QueryCaptionType.EXTRACTIVE)
                    .setHighlightEnabled(true)))
            .setSelect("HotelId", "HotelName", "Description");

        SearchPagedIterable results = searchClient.search(
            "walking distance to live music", searchOptions, null);

        System.out.printf("Found results with semantic search%n%n");
        int rowNumber = 1;

        for (SearchResult result : results) {
            var document = result.getDocument(SearchDocument.class);
            double rerankerScore = result.getSemanticSearch().getRerankerScore();

            System.out.printf("Search result #%d:%n", rowNumber++);
            System.out.printf("  Re-ranker Score: %.2f%n", rerankerScore);
            System.out.printf("  HotelName: %s%n", document.get("HotelName"));
            System.out.printf("  Description: %s%n%n",
                document.get("Description") != null ?
                    document.get("Description") : "N/A");

            // Handle captions
            List<QueryCaptionResult> captions =
                result.getSemanticSearch().getQueryCaptions();
            if (captions != null && !captions.isEmpty()) {
                QueryCaptionResult caption = captions.get(0);

                if (caption.getHighlights() != null &&
                    !caption.getHighlights().trim().isEmpty()) {
                    System.out.printf("  Caption with highlights: %s%n",
                        caption.getHighlights());
                } else if (caption.getText() != null &&
                    !caption.getText().trim().isEmpty()) {
                    System.out.printf("  Caption text: %s%n",
                        caption.getText());
                } else {
                    System.out.println(
                        "  Caption exists but has no text or highlights content");
                }
            } else {
                System.out.println("  No captions found for this result");
            }
            System.out.println("-".repeat(60));
        }

        System.exit(0);
    }
}
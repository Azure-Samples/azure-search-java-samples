package com.azure.search.quickstart;

import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.models.QueryAnswer;
import com.azure.search.documents.models.QueryAnswerResult;
import com.azure.search.documents.models.QueryAnswerType;
import com.azure.search.documents.models.QueryCaption;
import com.azure.search.documents.models.QueryCaptionResult;
import com.azure.search.documents.models.QueryCaptionType;
import com.azure.search.documents.models.QueryType;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;
import com.azure.search.documents.models.SemanticSearchOptions;
import com.azure.search.documents.util.SearchPagedIterable;

import java.util.List;

public class SemanticAnswer {
    public static void main(String[] args) {
        var searchClient = new SearchClientBuilder()
            .endpoint(SearchConfig.SEARCH_ENDPOINT)
            .indexName(SearchConfig.INDEX_NAME)
            .credential(SearchConfig.CREDENTIAL)
            .buildClient();

        var searchOptions = new SearchOptions()
            .setQueryType(QueryType.SEMANTIC)
            .setSemanticSearchOptions(new SemanticSearchOptions()
                .setSemanticConfigurationName(SearchConfig.SEMANTIC_CONFIG_NAME)
                .setQueryCaption(new QueryCaption(QueryCaptionType.EXTRACTIVE))
                .setQueryAnswer(new QueryAnswer(QueryAnswerType.EXTRACTIVE)))
            .setSelect("HotelName", "Description", "Category");

        SearchPagedIterable results = searchClient.search(
            "What's a good hotel for people who like to read",
            searchOptions, null);

        System.out.println("Answers:\n");

        // Extract semantic answers
        List<QueryAnswerResult> semanticAnswers =
            results.getSemanticResults().getQueryAnswers();
        int answerNumber = 1;

        if (semanticAnswers != null) {
            for (QueryAnswerResult answer : semanticAnswers) {
                System.out.printf("Semantic answer result #%d:%n",
                    answerNumber++);

                if (answer.getHighlights() != null &&
                    !answer.getHighlights().trim().isEmpty()) {
                    System.out.printf("Semantic Answer: %s%n",
                        answer.getHighlights());
                } else {
                    System.out.printf("Semantic Answer: %s%n", answer.getText());
                }
                System.out.printf("Semantic Answer Score: %.2f%n%n",
                    answer.getScore());
            }
        }

        System.out.println("Search Results:\n");
        int rowNumber = 1;

        // Iterate through search results
        for (SearchResult result : results) {
            var document = result.getDocument(SearchDocument.class);
            double rerankerScore = result.getSemanticSearch().getRerankerScore();

            System.out.printf("Search result #%d:%n", rowNumber++);
            System.out.printf("Re-ranker Score: %.2f%n", rerankerScore);
            System.out.printf("Hotel: %s%n", document.get("HotelName"));
            System.out.printf("Description: %s%n",
                document.get("Description") != null ?
                    document.get("Description") : "N/A");

            List<QueryCaptionResult> captions =
                result.getSemanticSearch().getQueryCaptions();
            if (captions != null && !captions.isEmpty()) {
                QueryCaptionResult caption = captions.get(0);
                if (caption.getHighlights() != null &&
                    !caption.getHighlights().trim().isEmpty()) {
                    System.out.printf("Caption: %s%n%n",
                        caption.getHighlights());
                } else {
                    System.out.printf("Caption: %s%n%n", caption.getText());
                }
            } else {
                System.out.println();
            }
        }

        System.exit(0);
    }
}
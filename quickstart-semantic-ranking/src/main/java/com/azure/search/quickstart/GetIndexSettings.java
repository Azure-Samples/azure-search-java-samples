package com.azure.search.quickstart;

import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.indexes.models.SearchField;
import com.azure.search.documents.indexes.models.SearchIndex;
import com.azure.search.documents.indexes.models.SemanticConfiguration;
import com.azure.search.documents.indexes.models.SemanticField;
import com.azure.search.documents.indexes.models.SemanticSearch;

public class GetIndexSettings {
    public static void main(String[] args) {
        var indexClient = new SearchIndexClientBuilder()
            .endpoint(SearchConfig.SEARCH_ENDPOINT)
            .credential(SearchConfig.CREDENTIAL)
            .buildClient();

        System.out.println("Getting semantic search index settings...");

        SearchIndex index = indexClient.getIndex(SearchConfig.INDEX_NAME);

        System.out.println("Index name: " + index.getName());
        System.out.println("Number of fields: " + index.getFields().size());

        for (SearchField field : index.getFields()) {
            System.out.printf("Field: %s, Type: %s, Searchable: %s%n",
                field.getName(), field.getType(), field.isSearchable());
        }

        SemanticSearch semanticSearch = index.getSemanticSearch();
        if (semanticSearch != null &&
            semanticSearch.getConfigurations() != null) {
            System.out.println("Semantic search configurations: " +
                semanticSearch.getConfigurations().size());
            for (SemanticConfiguration config :
                semanticSearch.getConfigurations()) {
                System.out.println("Configuration name: " + config.getName());
                SemanticField titleField = config.getPrioritizedFields().getTitleField();
                if (titleField != null) {
                    System.out.println("Title field: " +
                        titleField.getFieldName());
                }
            }
        } else {
            System.out.println(
                "No semantic configuration exists for this index.");
        }

        System.exit(0);
    }
}

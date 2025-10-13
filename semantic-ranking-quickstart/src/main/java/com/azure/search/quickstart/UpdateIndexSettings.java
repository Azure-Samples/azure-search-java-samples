package com.azure.search.quickstart;

import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.indexes.models.SearchIndex;
import com.azure.search.documents.indexes.models.SemanticConfiguration;
import com.azure.search.documents.indexes.models.SemanticField;
import com.azure.search.documents.indexes.models.SemanticPrioritizedFields;
import com.azure.search.documents.indexes.models.SemanticSearch;

import java.util.ArrayList;
import java.util.List;

public class UpdateIndexSettings {
    public static void main(String[] args) {
        try {
            var indexClient = new SearchIndexClientBuilder()
                .endpoint(SearchConfig.SEARCH_ENDPOINT)
                .credential(SearchConfig.CREDENTIAL)
                .buildClient();

            SearchIndex existingIndex =
                indexClient.getIndex(SearchConfig.INDEX_NAME);

            // Create prioritized fields for semantic configuration
            var prioritizedFields = new SemanticPrioritizedFields()
                .setTitleField(new SemanticField("HotelName"))
                .setKeywordsFields(List.of(new SemanticField("Tags")))
                .setContentFields(List.of(new SemanticField("Description")));

            var newSemanticConfiguration = new SemanticConfiguration(
                SearchConfig.SEMANTIC_CONFIG_NAME, prioritizedFields);

            // Add the semantic configuration to the index
            SemanticSearch semanticSearch = existingIndex.getSemanticSearch();
            if (semanticSearch == null) {
                semanticSearch = new SemanticSearch();
                existingIndex.setSemanticSearch(semanticSearch);
            }

            List<SemanticConfiguration> configurations =
                semanticSearch.getConfigurations();
            if (configurations == null) {
                configurations = new ArrayList<>();
                semanticSearch.setConfigurations(configurations);
            }

            // Check if configuration already exists
            boolean configExists = configurations.stream()
                .anyMatch(config -> SearchConfig.SEMANTIC_CONFIG_NAME
                    .equals(config.getName()));

            if (!configExists) {
                configurations.add(newSemanticConfiguration);
            }

            indexClient.createOrUpdateIndex(existingIndex);

            SearchIndex updatedIndex =
                indexClient.getIndex(SearchConfig.INDEX_NAME);

            System.out.println("Semantic configurations:");
            System.out.println("-".repeat(40));

            SemanticSearch updatedSemanticSearch =
                updatedIndex.getSemanticSearch();
            if (updatedSemanticSearch != null &&
                updatedSemanticSearch.getConfigurations() != null) {
                for (SemanticConfiguration config :
                    updatedSemanticSearch.getConfigurations()) {
                    System.out.println("Configuration name: " + config.getName());

                    SemanticPrioritizedFields fields =
                        config.getPrioritizedFields();
                    if (fields.getTitleField() != null) {
                        System.out.println("Title field: " +
                            fields.getTitleField().getFieldName());
                    }
                    if (fields.getKeywordsFields() != null) {
                        List<String> keywords = fields.getKeywordsFields().stream()
                            .map(SemanticField::getFieldName)
                            .toList();
                        System.out.println("Keywords fields: " +
                            String.join(", ", keywords));
                    }
                    if (fields.getContentFields() != null) {
                        List<String> content = fields.getContentFields().stream()
                            .map(SemanticField::getFieldName)
                            .toList();
                        System.out.println("Content fields: " +
                            String.join(", ", content));
                    }
                    System.out.println("-".repeat(40));
                }
            } else {
                System.out.println("No semantic configurations found.");
            }

            System.out.println("Semantic configuration updated successfully.");

            System.exit(0);
        } catch (Exception e) {
            System.err.println("Error updating semantic configuration: " +
                e.getMessage());
        }
    }
}

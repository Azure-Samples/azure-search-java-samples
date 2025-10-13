package com.example.rag;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;

import java.util.List;

public class Query {
    private static SearchClient getSearchClient() {
        String searchEndpoint = System.getenv("AZURE_SEARCH_ENDPOINT");
        String searchIndex = System.getenv("AZURE_SEARCH_INDEX_NAME");

        return new SearchClientBuilder()
                .endpoint(searchEndpoint)
                .indexName(searchIndex)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    private static OpenAIClient getOpenAIClient() {
        String openaiEndpoint = System.getenv("AZURE_OPENAI_ENDPOINT");

        return new OpenAIClientBuilder()
                .endpoint(openaiEndpoint)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    private static List<SearchResult> searchDocuments(
        SearchClient searchClient, String query) {
        var searchOptions = new SearchOptions()
                .setTop(5)
                .setQueryType(com.azure.search.documents.models.QueryType.SEMANTIC)
                .setSemanticSearchOptions(new com.azure.search.documents.models.SemanticSearchOptions()
                        .setSemanticConfigurationName("semantic-config"))
                .setSelect("HotelName", "Description", "Tags");

        return searchClient.search(query, searchOptions, null)
                .stream()
                .limit(5)
                .toList();
    }

    private static String queryOpenAI(OpenAIClient openAIClient,
        String userQuery, List<SearchResult> sources) {
        String deploymentModel = System.getenv("AZURE_DEPLOYMENT_MODEL");

        String sourcesText = sources.stream()
                .map(source -> source.getDocument(Object.class).toString())
                .collect(java.util.stream.Collectors.joining("\n"));

        var messages = List.of(
                new ChatRequestSystemMessage("""
                    You are an assistant that recommends hotels based on 
                    search results."""),
                new ChatRequestUserMessage("""
                    Can you recommend a few hotels that offer %s?
                    Here are the search results:
                    %s""".formatted(userQuery, sourcesText))
        );

        var chatOptions = new ChatCompletionsOptions(messages);
        ChatCompletions response = openAIClient.getChatCompletions(deploymentModel, chatOptions);

        return response.getChoices().get(0).getMessage().getContent();
    }

    public static void main(String[] args) {
        SearchClient searchClient = getSearchClient();
        OpenAIClient openAIClient = getOpenAIClient();

        String userQuery = "complimentary breakfast";
        List<SearchResult> sources = searchDocuments(searchClient, userQuery);
        String response = queryOpenAI(openAIClient, userQuery, sources);

        System.out.println(response);
        System.exit(0);
    }
}

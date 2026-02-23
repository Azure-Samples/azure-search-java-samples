import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.indexes.models.*;
import com.azure.search.documents.knowledgebases.SearchKnowledgeBaseClient;
import com.azure.search.documents.knowledgebases.SearchKnowledgeBaseClientBuilder;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseActivityRecord;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseMessage;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseMessageTextContent;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseReference;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalRequest;
import com.azure.search.documents.knowledgebases.models.KnowledgeBaseRetrievalResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class AgenticRetrievalQuickstart {

    public static void main(String[] args) throws Exception {
        // Load environment variables from the .env file
        // Ensure your .env file is in the same directory with the required variables
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String searchEndpoint = dotenv.get("SEARCH_ENDPOINT");
        if (searchEndpoint == null) {
            throw new IllegalStateException("SEARCH_ENDPOINT isn't set.");
        }
        String aoaiEndpoint = dotenv.get("AOAI_ENDPOINT");
        if (aoaiEndpoint == null) {
            throw new IllegalStateException("AOAI_ENDPOINT isn't set.");
        }

        String aoaiEmbeddingModel = "text-embedding-3-large";
        String aoaiEmbeddingDeployment = "text-embedding-3-large";
        String aoaiGptModel = "gpt-5-mini";
        String aoaiGptDeployment = "gpt-5-mini";

        String indexName = "earth-at-night";
        String knowledgeSourceName = "earth-knowledge-source";
        String knowledgeBaseName = "earth-knowledge-base";

        TokenCredential credential = new DefaultAzureCredentialBuilder().build();

        // Define fields for the index
        List<SearchField> fields = Arrays.asList(
            new SearchField("id", SearchFieldDataType.STRING)
                .setKey(true)
                .setFilterable(true)
                .setSortable(true)
                .setFacetable(true),
            new SearchField("page_chunk", SearchFieldDataType.STRING)
                .setFilterable(false)
                .setSortable(false)
                .setFacetable(false),
            new SearchField("page_embedding_text_3_large",
                    SearchFieldDataType.collection(SearchFieldDataType.SINGLE))
                .setVectorSearchDimensions(3072)
                .setVectorSearchProfileName("hnsw_text_3_large"),
            new SearchField("page_number", SearchFieldDataType.INT32)
                .setFilterable(true)
                .setSortable(true)
                .setFacetable(true)
        );

        // Define a vectorizer
        AzureOpenAIVectorizer vectorizer = new AzureOpenAIVectorizer(
                "azure_openai_text_3_large")
            .setParameters(new AzureOpenAIVectorizerParameters()
                .setResourceUrl(aoaiEndpoint)
                .setDeploymentName(aoaiEmbeddingDeployment)
                .setModelName(AzureOpenAIModelName.fromString(aoaiEmbeddingModel)));

        // Define a vector search profile and algorithm
        VectorSearch vectorSearch = new VectorSearch()
            .setProfiles(Arrays.asList(
                new VectorSearchProfile("hnsw_text_3_large", "alg")
                    .setVectorizerName("azure_openai_text_3_large")
            ))
            .setAlgorithms(Arrays.asList(
                new HnswAlgorithmConfiguration("alg")
            ))
            .setVectorizers(Arrays.asList(vectorizer));

        // Define a semantic configuration
        SemanticSearch semanticSearch = new SemanticSearch()
            .setDefaultConfigurationName("semantic_config")
            .setConfigurations(Arrays.asList(
                new SemanticConfiguration("semantic_config",
                    new SemanticPrioritizedFields()
                        .setContentFields(Arrays.asList(
                            new SemanticField("page_chunk")
                        ))
                )
            ));

        // Create the index
        SearchIndex index = new SearchIndex(indexName)
            .setFields(fields)
            .setVectorSearch(vectorSearch)
            .setSemanticSearch(semanticSearch);

        // Create the index client, deleting and recreating the index if it exists
        SearchIndexClient indexClient = new SearchIndexClientBuilder()
            .endpoint(searchEndpoint)
            .credential(credential)
            .buildClient();

        indexClient.createOrUpdateIndex(index);
        System.out.println("Index '" + indexName + "' created or updated successfully.");

        // Upload sample documents from the GitHub URL
        String url = "https://raw.githubusercontent.com/Azure-Samples/"
            + "azure-search-sample-data/refs/heads/main/nasa-e-book/"
            + "earth-at-night-json/documents.json";

        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        java.net.http.HttpResponse<String> response = httpClient.send(httpRequest,
            java.net.http.HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch documents: " + response.statusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonArray = mapper.readTree(response.body());

        List<SearchDocument> documents = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonNode doc = jsonArray.get(i);
            SearchDocument searchDoc = new SearchDocument();

            searchDoc.put("id", doc.has("id")
                ? doc.get("id").asText() : String.valueOf(i + 1));
            searchDoc.put("page_chunk", doc.has("page_chunk")
                ? doc.get("page_chunk").asText() : "");

            if (doc.has("page_embedding_text_3_large")
                    && doc.get("page_embedding_text_3_large").isArray()) {
                List<Double> embeddings = new ArrayList<>();
                for (JsonNode embedding : doc.get("page_embedding_text_3_large")) {
                    embeddings.add(embedding.asDouble());
                }
                searchDoc.put("page_embedding_text_3_large", embeddings);
            } else {
                List<Double> fallback = new ArrayList<>();
                for (int j = 0; j < 3072; j++) {
                    fallback.add(0.1);
                }
                searchDoc.put("page_embedding_text_3_large", fallback);
            }

            searchDoc.put("page_number", doc.has("page_number")
                ? doc.get("page_number").asInt() : i + 1);

            documents.add(searchDoc);
        }

        SearchClient searchClient = new SearchClientBuilder()
            .endpoint(searchEndpoint)
            .indexName(indexName)
            .credential(credential)
            .buildClient();

        searchClient.uploadDocuments(documents);
        System.out.println("Documents uploaded to index '" + indexName + "' successfully.");

        // Create a knowledge source
        SearchIndexKnowledgeSource indexKnowledgeSource =
            new SearchIndexKnowledgeSource(
                knowledgeSourceName,
                new SearchIndexKnowledgeSourceParameters(indexName)
                    .setSourceDataFields(Arrays.asList(
                        new SearchIndexFieldReference("id"),
                        new SearchIndexFieldReference("page_chunk"),
                        new SearchIndexFieldReference("page_number")
                    ))
            );

        indexClient.createOrUpdateKnowledgeSource(indexKnowledgeSource);
        System.out.println("Knowledge source '" + knowledgeSourceName
            + "' created or updated successfully.");

        // Create a knowledge base
        AzureOpenAIVectorizerParameters openAiParameters =
            new AzureOpenAIVectorizerParameters()
                .setResourceUrl(aoaiEndpoint)
                .setDeploymentName(aoaiGptDeployment)
                .setModelName(AzureOpenAIModelName.fromString(aoaiGptModel));

        KnowledgeBaseAzureOpenAIModel model =
            new KnowledgeBaseAzureOpenAIModel(openAiParameters);

        KnowledgeBase knowledgeBase = new KnowledgeBase(
                knowledgeBaseName,
                Arrays.asList(new KnowledgeSourceReference(knowledgeSourceName))
            )
            .setRetrievalReasoningEffort(
                new KnowledgeRetrievalLowReasoningEffort())
            .setOutputMode(
                KnowledgeRetrievalOutputMode.ANSWER_SYNTHESIS)
            .setAnswerInstructions(
                "Provide a two sentence concise and informative answer "
                + "based on the retrieved documents.")
            .setModels(Arrays.asList(model));

        indexClient.createOrUpdateKnowledgeBase(knowledgeBase);
        System.out.println("Knowledge base '" + knowledgeBaseName
            + "' created or updated successfully.");

        // Set up messages
        String instructions = "A Q&A agent that can answer questions about the "
            + "Earth at night.\n"
            + "If you don't have the answer, respond with \"I don't know\".";

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", instructions);
        messages.add(systemMessage);

        // Run agentic retrieval
        SearchKnowledgeBaseClient baseClient =
            new SearchKnowledgeBaseClientBuilder()
                .endpoint(searchEndpoint)
                .knowledgeBaseName(knowledgeBaseName)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();

        String query = "Why do suburban belts display larger December brightening "
            + "than urban cores even though absolute light levels are higher "
            + "downtown? Why is the Phoenix nighttime street grid is so sharply "
            + "visible from space, whereas large stretches of the interstate "
            + "between midwestern cities remain comparatively dim?";

        messages.add(Map.of("role", "user", "content", query));

        System.out.println("Running the query..." + query);
        KnowledgeBaseRetrievalResponse retrievalResult = retrieve(baseClient, messages);

        String responseText = ((KnowledgeBaseMessageTextContent)
            retrievalResult.getResponse().get(0).getContent().get(0)).getText();
        messages.add(Map.of("role", "assistant", "content", responseText));

        // Print the response, activity, and references
        printResult(responseText, retrievalResult);

        // Continue the conversation
        String nextQuery = "How do I find lava at night?";
        System.out.println("Continue the conversation with this query: " + nextQuery);
        messages.add(Map.of("role", "user", "content", nextQuery));

        retrievalResult = retrieve(baseClient, messages);

        responseText = ((KnowledgeBaseMessageTextContent)
            retrievalResult.getResponse().get(0).getContent().get(0)).getText();
        messages.add(Map.of("role", "assistant", "content", responseText));

        // Print the new response, activity, and references
        printResult(responseText, retrievalResult);

        // Clean up resources
        indexClient.deleteKnowledgeBase(knowledgeBaseName);
        System.out.println("Knowledge base '" + knowledgeBaseName
            + "' deleted successfully.");

        indexClient.deleteKnowledgeSource(knowledgeSourceName);
        System.out.println("Knowledge source '" + knowledgeSourceName
            + "' deleted successfully.");

        indexClient.deleteIndex(indexName);
        System.out.println("Index '" + indexName + "' deleted successfully.");
    }

    private static KnowledgeBaseRetrievalResponse retrieve(
            SearchKnowledgeBaseClient client,
            List<Map<String, String>> messages) {
        List<KnowledgeBaseMessage> kbMessages = new ArrayList<>();
        for (Map<String, String> msg : messages) {
            if (!"system".equals(msg.get("role"))) {
                kbMessages.add(
                    new KnowledgeBaseMessage(Arrays.asList(
                        new KnowledgeBaseMessageTextContent(
                            msg.get("content"))
                    )).setRole(msg.get("role"))
                );
            }
        }

        KnowledgeBaseRetrievalRequest request =
            new KnowledgeBaseRetrievalRequest();
        request.setMessages(kbMessages);
        request.setRetrievalReasoningEffort(
            new com.azure.search.documents.knowledgebases.models
                .KnowledgeRetrievalLowReasoningEffort());

        return client.retrieve(request, null);
    }

    private static String toJsonString(com.azure.json.JsonSerializable<?> obj) {
        try {
            java.io.StringWriter sw = new java.io.StringWriter();
            try (com.azure.json.JsonWriter jw =
                    com.azure.json.JsonProviders.createWriter(sw)) {
                obj.toJson(jw);
            }
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(sw.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(json);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private static void printResult(String responseText,
            KnowledgeBaseRetrievalResponse result) {
        System.out.println("Response:");
        System.out.println(responseText);

        System.out.println("Activity:");
        for (KnowledgeBaseActivityRecord activity : result.getActivity()) {
            System.out.println("Activity Type: "
                + activity.getClass().getSimpleName());
            System.out.println(toJsonString(activity));
        }

        System.out.println("References:");
        for (KnowledgeBaseReference reference : result.getReferences()) {
            System.out.println("Reference Type: "
                + reference.getClass().getSimpleName());
            System.out.println(toJsonString(reference));
        }
    }
}

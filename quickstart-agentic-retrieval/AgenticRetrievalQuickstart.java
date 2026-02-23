import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.util.BinaryData;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.indexes.models.*;
import com.azure.search.documents.agents.SearchKnowledgeAgentClient;
import com.azure.search.documents.agents.SearchKnowledgeAgentClientBuilder;
import com.azure.search.documents.agents.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest.Builder;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AgenticRetrievalQuickstart {

    // Configuration - Update these values for your environment
    private static final String SEARCH_ENDPOINT;
    private static final String AZURE_OPENAI_ENDPOINT;
    private static final String AZURE_OPENAI_GPT_DEPLOYMENT;
    private static final String AZURE_OPENAI_GPT_MODEL = "gpt-5-mini";
    private static final String AZURE_OPENAI_EMBEDDING_DEPLOYMENT;
    private static final String AZURE_OPENAI_EMBEDDING_MODEL = "text-embedding-3-large";
    private static final String INDEX_NAME = "earth_at_night";
    private static final String AGENT_NAME = "earth-search-agent";
    private static final String SEARCH_API_VERSION = "2025-05-01-Preview";

    static {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        SEARCH_ENDPOINT = getEnvVar(dotenv, "AZURE_SEARCH_ENDPOINT", 
            "https://contoso-agentic-search-service.search.windows.net");
        AZURE_OPENAI_ENDPOINT = getEnvVar(dotenv, "AZURE_OPENAI_ENDPOINT",
            "https://contoso-proj-agentic-foundry-res.openai.azure.com/");
        AZURE_OPENAI_GPT_DEPLOYMENT = getEnvVar(dotenv, "AZURE_OPENAI_GPT_DEPLOYMENT", "gpt-5-mini");
        AZURE_OPENAI_EMBEDDING_DEPLOYMENT = getEnvVar(dotenv, "AZURE_OPENAI_EMBEDDING_DEPLOYMENT", "text-embedding-3-large");
    }

    private static String getEnvVar(Dotenv dotenv, String key, String defaultValue) {
        String value = dotenv.get(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public static void main(String[] args) {
        try {
            System.out.println("Starting Azure AI Search agentic retrieval quickstart...\n");

            // Initialize Azure credentials using managed identity (recommended)
            TokenCredential credential = new DefaultAzureCredentialBuilder().build();

            // Create search clients
            SearchIndexClient searchIndexClient = new SearchIndexClientBuilder()
                .endpoint(SEARCH_ENDPOINT)
                .credential(credential)
                .buildClient();

            SearchClient searchClient = new SearchClientBuilder()
                .endpoint(SEARCH_ENDPOINT)
                .indexName(INDEX_NAME)
                .credential(credential)
                .buildClient();

            // Create Azure OpenAI client
            OpenAIAsyncClient openAIClient = new OpenAIClientBuilder()
                .endpoint(AZURE_OPENAI_ENDPOINT)
                .credential(credential)
                .buildAsyncClient();

            // Step 1: Create search index with vector and semantic capabilities
            createSearchIndex(searchIndexClient);

            // Step 2: Upload documents
            uploadDocuments(searchClient);

            // Step 3: Create knowledge agent
            createKnowledgeAgent(credential);

            // Step 4: Run agentic retrieval with conversation
            runAgenticRetrieval(credential, openAIClient);

            // Step 5: Clean up - Delete knowledge agent and search index
            deleteKnowledgeAgent(credential);
            deleteSearchIndex(searchIndexClient);

            System.out.println("[DONE] Quickstart completed successfully!");

        } catch (Exception e) {
            System.err.println("[ERROR] Error in main execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createSearchIndex(SearchIndexClient indexClient) {
        System.out.println("[WAIT] Creating search index...");

        try {
            // Delete index if it exists
            try {
                indexClient.deleteIndex(INDEX_NAME);
                System.out.println("[DELETE] Deleted existing index '" + INDEX_NAME + "'");
            } catch (Exception e) {
                // Index doesn't exist, which is fine
            }

            // Define fields
            List<SearchField> fields = Arrays.asList(
                new SearchField("id", SearchFieldDataType.STRING)
                    .setKey(true)
                    .setFilterable(true)
                    .setSortable(true)
                    .setFacetable(true),
                new SearchField("page_chunk", SearchFieldDataType.STRING)
                    .setSearchable(true)
                    .setFilterable(false)
                    .setSortable(false)
                    .setFacetable(false),
                new SearchField("page_embedding_text_3_large", SearchFieldDataType.collection(SearchFieldDataType.SINGLE))
                    .setSearchable(true)
                    .setFilterable(false)
                    .setSortable(false)
                    .setFacetable(false)
                    .setVectorSearchDimensions(3072)
                    .setVectorSearchProfileName("hnsw_text_3_large"),
                new SearchField("page_number", SearchFieldDataType.INT32)
                    .setFilterable(true)
                    .setSortable(true)
                    .setFacetable(true)
            );

            // Create vectorizer
            AzureOpenAIVectorizer vectorizer = new AzureOpenAIVectorizer("azure_openai_text_3_large")
                .setParameters(new AzureOpenAIVectorizerParameters()
                    .setResourceUrl(AZURE_OPENAI_ENDPOINT)
                    .setDeploymentName(AZURE_OPENAI_EMBEDDING_DEPLOYMENT)
                    .setModelName(AzureOpenAIModelName.TEXT_EMBEDDING_3_LARGE));

            // Create vector search configuration
            VectorSearch vectorSearch = new VectorSearch()
                .setProfiles(Arrays.asList(
                    new VectorSearchProfile("hnsw_text_3_large", "alg")
                        .setVectorizerName("azure_openai_text_3_large")
                ))
                .setAlgorithms(Arrays.asList(
                    new HnswAlgorithmConfiguration("alg")
                ))
                .setVectorizers(Arrays.asList(vectorizer));

            // Create semantic search configuration
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
            SearchIndex index = new SearchIndex(INDEX_NAME)
                .setFields(fields)
                .setVectorSearch(vectorSearch)
                .setSemanticSearch(semanticSearch);

            indexClient.createOrUpdateIndex(index);
            System.out.println("[DONE] Index '" + INDEX_NAME + "' created successfully.");

        } catch (Exception e) {
            System.err.println("[ERROR] Error creating index: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void uploadDocuments(SearchClient searchClient) {
        System.out.println("[WAIT] Uploading documents...");

        try {
            // Fetch documents from GitHub
            List<SearchDocument> documents = fetchEarthAtNightDocuments();

            searchClient.uploadDocuments(documents);
            System.out.println("[DONE] Uploaded " + documents.size() + " documents successfully.");

            // Wait for indexing to complete
            System.out.println("[WAIT] Waiting for document indexing to complete...");
            Thread.sleep(5000);
            System.out.println("[DONE] Document indexing completed.");

        } catch (Exception e) {
            System.err.println("[ERROR] Error uploading documents: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static List<SearchDocument> fetchEarthAtNightDocuments() {
        System.out.println("[WAIT] Fetching Earth at Night documents from GitHub...");

        String documentsUrl = "https://raw.githubusercontent.com/Azure-Samples/azure-search-sample-data/refs/heads/main/nasa-e-book/earth-at-night-json/documents.json";

        try {
            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(documentsUrl))
                .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request, 
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

                searchDoc.put("id", doc.has("id") ? doc.get("id").asText() : String.valueOf(i + 1));
                searchDoc.put("page_chunk", doc.has("page_chunk") ? doc.get("page_chunk").asText() : "");

                // Handle embeddings
                if (doc.has("page_embedding_text_3_large") && doc.get("page_embedding_text_3_large").isArray()) {
                    List<Double> embeddings = new ArrayList<>();
                    for (JsonNode embedding : doc.get("page_embedding_text_3_large")) {
                        embeddings.add(embedding.asDouble());
                    }
                    searchDoc.put("page_embedding_text_3_large", embeddings);
                } else {
                    // Fallback embeddings
                    List<Double> fallbackEmbeddings = new ArrayList<>();
                    for (int j = 0; j < 3072; j++) {
                        fallbackEmbeddings.add(0.1);
                    }
                    searchDoc.put("page_embedding_text_3_large", fallbackEmbeddings);
                }

                searchDoc.put("page_number", doc.has("page_number") ? doc.get("page_number").asInt() : i + 1);

                documents.add(searchDoc);
            }

            System.out.println("[DONE] Fetched " + documents.size() + " documents from GitHub");
            return documents;

        } catch (Exception e) {
            System.err.println("[ERROR] Error fetching documents from GitHub: " + e.getMessage());
            System.out.println("🔄 Falling back to sample documents...");

            // Fallback to sample documents
            List<SearchDocument> fallbackDocs = new ArrayList<>();

            SearchDocument doc1 = new SearchDocument();
            doc1.put("id", "1");
            doc1.put("page_chunk", "The Earth at night reveals the patterns of human settlement and economic activity. City lights trace the contours of civilization, creating a luminous map of where people live and work.");
            List<Double> embeddings1 = new ArrayList<>();
            for (int i = 0; i < 3072; i++) {
                embeddings1.add(0.1);
            }
            doc1.put("page_embedding_text_3_large", embeddings1);
            doc1.put("page_number", 1);

            SearchDocument doc2 = new SearchDocument();
            doc2.put("id", "2");
            doc2.put("page_chunk", "From space, the aurora borealis appears as shimmering curtains of green and blue light dancing across the polar regions.");
            List<Double> embeddings2 = new ArrayList<>();
            for (int i = 0; i < 3072; i++) {
                embeddings2.add(0.2);
            }
            doc2.put("page_embedding_text_3_large", embeddings2);
            doc2.put("page_number", 2);

            fallbackDocs.add(doc1);
            fallbackDocs.add(doc2);

            return fallbackDocs;
        }
    }

    private static void createKnowledgeAgent(TokenCredential credential) {
        System.out.println("[WAIT] Creating knowledge agent...");

        // Delete agent if it exists
        deleteKnowledgeAgent(credential);

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode agentDefinition = mapper.createObjectNode();
            agentDefinition.put("name", AGENT_NAME);
            agentDefinition.put("description", "Knowledge agent for Earth at Night e-book content");

            ObjectNode model = mapper.createObjectNode();
            model.put("kind", "azureOpenAI");
            ObjectNode azureOpenAIParams = mapper.createObjectNode();
            azureOpenAIParams.put("resourceUri", AZURE_OPENAI_ENDPOINT);
            azureOpenAIParams.put("deploymentId", AZURE_OPENAI_GPT_DEPLOYMENT);
            azureOpenAIParams.put("modelName", AZURE_OPENAI_GPT_MODEL);
            model.set("azureOpenAIParameters", azureOpenAIParams);
            agentDefinition.set("models", mapper.createArrayNode().add(model));

            ObjectNode targetIndex = mapper.createObjectNode();
            targetIndex.put("indexName", INDEX_NAME);
            targetIndex.put("defaultRerankerThreshold", 2.5);
            agentDefinition.set("targetIndexes", mapper.createArrayNode().add(targetIndex));

            String token = getAccessToken(credential, "https://search.azure.com/.default");

            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(SEARCH_ENDPOINT + "/agents/" + AGENT_NAME + "?api-version=" + SEARCH_API_VERSION))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(java.net.http.HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(agentDefinition)))
                .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request,
                java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Failed to create knowledge agent: " + response.statusCode() + " " + response.body());
            }

            System.out.println("[DONE] Knowledge agent '" + AGENT_NAME + "' created successfully.");

        } catch (Exception e) {
            System.err.println("[ERROR] Error creating knowledge agent: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void runAgenticRetrieval(TokenCredential credential, OpenAIAsyncClient openAIClient) {
        System.out.println("[SEARCH] Running agentic retrieval...");

        // Initialize messages with system instructions
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "A Q&A agent that can answer questions about the Earth at night.\n" +
            "Sources have a JSON format with a ref_id that must be cited in the answer.\n" +
            "If you do not have the answer, respond with \"I don't know\".");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "Why do suburban belts display larger December brightening than urban cores even though absolute light levels are higher downtown? Why is the Phoenix nighttime street grid is so sharply visible from space, whereas large stretches of the interstate between midwestern cities remain comparatively dim?");
        messages.add(userMessage);

        try {
            // Call agentic retrieval API (excluding system message)
            List<Map<String, String>> userMessages = messages.stream()
                .filter(m -> !"system".equals(m.get("role")))
                .collect(java.util.stream.Collectors.toList());

            String retrievalResponse = callAgenticRetrieval(credential, userMessages);

            // Add assistant response to conversation history
            Map<String, String> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", retrievalResponse);
            messages.add(assistantMessage);

            System.out.println(retrievalResponse);

            // Now do chat completion with full conversation history
            generateFinalAnswer(openAIClient, messages);

            // Continue conversation with second question
            continueConversation(credential, openAIClient, messages);

        } catch (Exception e) {
            System.err.println("[ERROR] Error in agentic retrieval: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static String callAgenticRetrieval(TokenCredential credential, List<Map<String, String>> messages) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode retrievalRequest = mapper.createObjectNode();

            // Convert messages to the correct format expected by the Knowledge agent
            com.fasterxml.jackson.databind.node.ArrayNode agentMessages = mapper.createArrayNode();
            for (Map<String, String> msg : messages) {
                ObjectNode agentMessage = mapper.createObjectNode();
                agentMessage.put("role", msg.get("role"));

                com.fasterxml.jackson.databind.node.ArrayNode content = mapper.createArrayNode();
                ObjectNode textContent = mapper.createObjectNode();
                textContent.put("type", "text");
                textContent.put("text", msg.get("content"));
                content.add(textContent);
                agentMessage.set("content", content);

                agentMessages.add(agentMessage);
            }
            retrievalRequest.set("messages", agentMessages);

            com.fasterxml.jackson.databind.node.ArrayNode targetIndexParams = mapper.createArrayNode();
            ObjectNode indexParam = mapper.createObjectNode();
            indexParam.put("indexName", INDEX_NAME);
            indexParam.put("rerankerThreshold", 2.5);
            indexParam.put("maxDocsForReranker", 100);
            indexParam.put("includeReferenceSourceData", true);
            targetIndexParams.add(indexParam);
            retrievalRequest.set("targetIndexParams", targetIndexParams);

            String token = getAccessToken(credential, "https://search.azure.com/.default");

            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(SEARCH_ENDPOINT + "/agents/" + AGENT_NAME + "/retrieve?api-version=" + SEARCH_API_VERSION))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(retrievalRequest)))
                .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request,
                java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Agentic retrieval failed: " + response.statusCode() + " " + response.body());
            }

            JsonNode responseJson = mapper.readTree(response.body());

            // Log activities and results
            logActivitiesAndResults(responseJson);

            // Extract response content
            if (responseJson.has("response") && responseJson.get("response").isArray()) {
                com.fasterxml.jackson.databind.node.ArrayNode responseArray = (com.fasterxml.jackson.databind.node.ArrayNode) responseJson.get("response");
                if (responseArray.size() > 0) {
                    JsonNode firstResponse = responseArray.get(0);
                    if (firstResponse.has("content") && firstResponse.get("content").isArray()) {
                        com.fasterxml.jackson.databind.node.ArrayNode contentArray = (com.fasterxml.jackson.databind.node.ArrayNode) firstResponse.get("content");
                        if (contentArray.size() > 0) {
                            JsonNode textContent = contentArray.get(0);
                            if (textContent.has("text")) {
                                return textContent.get("text").asText();
                            }
                        }
                    }
                }
            }

            return "No response content available";

        } catch (Exception e) {
            System.err.println("[ERROR] Error in agentic retrieval call: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void logActivitiesAndResults(JsonNode responseJson) {
        ObjectMapper mapper = new ObjectMapper();

        // Log activities
        System.out.println("\nActivities:");
        if (responseJson.has("activity") && responseJson.get("activity").isArray()) {
            for (JsonNode activity : responseJson.get("activity")) {
                String activityType = "UnknownActivityRecord";
                if (activity.has("InputTokens")) {
                    activityType = "KnowledgeAgentModelQueryPlanningActivityRecord";
                } else if (activity.has("TargetIndex")) {
                    activityType = "KnowledgeAgentSearchActivityRecord";
                } else if (activity.has("QueryTime")) {
                    activityType = "KnowledgeAgentSemanticRankerActivityRecord";
                }

                System.out.println("Activity Type: " + activityType);
                try {
                    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(activity));
                } catch (Exception e) {
                    System.out.println(activity.toString());
                }
            }
        }

        // Log results
        System.out.println("Results");
        if (responseJson.has("references") && responseJson.get("references").isArray()) {
            for (JsonNode reference : responseJson.get("references")) {
                String referenceType = "KnowledgeAgentAzureSearchDocReference";

                System.out.println("Reference Type: " + referenceType);
                try {
                    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reference));
                } catch (Exception e) {
                    System.out.println(reference.toString());
                }
            }
        }
    }

    private static void generateFinalAnswer(OpenAIAsyncClient openAIClient, List<Map<String, String>> messages) {
        System.out.println("\n[ASSISTANT]: ");

        try {
            List<ChatRequestMessage> chatMessages = new ArrayList<>();
            for (Map<String, String> msg : messages) {
                String role = msg.get("role");
                String content = msg.get("content");

                switch (role) {
                    case "system":
                        chatMessages.add(new ChatRequestSystemMessage(content));
                        break;
                    case "user":
                        chatMessages.add(new ChatRequestUserMessage(content));
                        break;
                    case "assistant":
                        chatMessages.add(new ChatRequestAssistantMessage(content));
                        break;
                }
            }

            ChatCompletionsOptions chatOptions = new ChatCompletionsOptions(chatMessages)
                .setMaxTokens(1000)
                .setTemperature(0.7);

            ChatCompletions completion = openAIClient.getChatCompletions(AZURE_OPENAI_GPT_DEPLOYMENT, chatOptions).block();

            if (completion != null && completion.getChoices() != null && !completion.getChoices().isEmpty()) {
                String answer = completion.getChoices().get(0).getMessage().getContent();
                System.out.println(answer.replace(".", "\n"));

                // Add this response to conversation history
                Map<String, String> assistantResponse = new HashMap<>();
                assistantResponse.put("role", "assistant");
                assistantResponse.put("content", answer);
                messages.add(assistantResponse);
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Error generating final answer: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void continueConversation(TokenCredential credential, OpenAIAsyncClient openAIClient, List<Map<String, String>> messages) {
        System.out.println("\n === Continuing Conversation ===");

        // Add follow-up question
        String followUpQuestion = "How do I find lava at night?";
        System.out.println("[QUESTION] Follow-up question: " + followUpQuestion);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", followUpQuestion);
        messages.add(userMessage);

        try {
            // FILTER OUT SYSTEM MESSAGE - only send user/assistant messages to agentic retrieval
            List<Map<String, String>> userAssistantMessages = messages.stream()
                .filter(m -> !"system".equals(m.get("role")))
                .collect(java.util.stream.Collectors.toList());

            String newRetrievalResponse = callAgenticRetrieval(credential, userAssistantMessages);

            // Add assistant response to conversation history
            Map<String, String> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", newRetrievalResponse);
            messages.add(assistantMessage);

            System.out.println(newRetrievalResponse);

            // Generate final answer for follow-up
            generateFinalAnswer(openAIClient, messages);

            System.out.println("\n === Conversation Complete ===");

        } catch (Exception e) {
            System.err.println("[ERROR] Error in conversation continuation: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void deleteKnowledgeAgent(TokenCredential credential) {
        System.out.println("[DELETE] Deleting knowledge agent...");

        try {
            String token = getAccessToken(credential, "https://search.azure.com/.default");

            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(SEARCH_ENDPOINT + "/agents/" + AGENT_NAME + "?api-version=" + SEARCH_API_VERSION))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request,
                java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                System.out.println("[INFO] Knowledge agent '" + AGENT_NAME + "' does not exist or was already deleted.");
                return;
            }

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Failed to delete knowledge agent: " + response.statusCode() + " " + response.body());
            }

            System.out.println("[DONE] Knowledge agent '" + AGENT_NAME + "' deleted successfully.");

        } catch (Exception e) {
            System.err.println("[ERROR] Error deleting knowledge agent: " + e.getMessage());
            // Don't throw - this is cleanup
        }
    }

    private static void deleteSearchIndex(SearchIndexClient indexClient) {
        System.out.println("[DELETE] Deleting search index...");

        try {
            indexClient.deleteIndex(INDEX_NAME);
            System.out.println("[DONE] Search index '" + INDEX_NAME + "' deleted successfully.");

        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().contains("404") || e.getMessage().contains("IndexNotFound"))) {
                System.out.println("[INFO] Search index '" + INDEX_NAME + "' does not exist or was already deleted.");
                return;
            }
            System.err.println("[ERROR] Error deleting search index: " + e.getMessage());
            // Don't throw - this is cleanup
        }
    }

    private static String getAccessToken(TokenCredential credential, String scope) {
        try {
            return credential.getToken(new com.azure.core.credential.TokenRequestContext().addScopes(scope)).block().getToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token", e);
        }
    }
}
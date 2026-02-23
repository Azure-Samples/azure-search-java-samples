import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.indexes.models.*;
import com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClient;
import com.azure.search.documents.knowledgebases.KnowledgeBaseRetrievalClientBuilder;
import com.azure.search.documents.knowledgebases.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class AgenticRetrievalQuickstart {

    // Configuration
    private static final String SEARCH_ENDPOINT;
    private static final String AZURE_OPENAI_ENDPOINT;
    private static final String AZURE_OPENAI_GPT_DEPLOYMENT = "gpt-5-mini";
    private static final String AZURE_OPENAI_GPT_MODEL = "gpt-5-mini";
    private static final String AZURE_OPENAI_EMBEDDING_DEPLOYMENT = "text-embedding-3-large";
    private static final String INDEX_NAME = "earth-at-night";
    private static final String KNOWLEDGE_SOURCE_NAME = "earth-knowledge-source";
    private static final String KNOWLEDGE_BASE_NAME = "earth-knowledge-base";

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        SEARCH_ENDPOINT = dotenv.get("SEARCH_ENDPOINT",
            "https://contoso-search.search.windows.net");
        AZURE_OPENAI_ENDPOINT = dotenv.get("AOAI_ENDPOINT",
            "https://contoso-openai.openai.azure.com/");
    }

    public static void main(String[] args) {
        try {
            System.out.println("Starting Azure AI Search agentic retrieval quickstart...\n");

            TokenCredential credential = new DefaultAzureCredentialBuilder().build();

            SearchIndexClient indexClient = new SearchIndexClientBuilder()
                .endpoint(SEARCH_ENDPOINT)
                .credential(credential)
                .buildClient();

            SearchClient searchClient = new SearchClientBuilder()
                .endpoint(SEARCH_ENDPOINT)
                .indexName(INDEX_NAME)
                .credential(credential)
                .buildClient();

            // Step 1: Create search index
            createSearchIndex(indexClient);

            // Step 2: Upload documents
            uploadDocuments(searchClient);

            // Step 3: Create knowledge source
            createKnowledgeSource(indexClient);

            // Step 4: Create knowledge base
            createKnowledgeBase(indexClient);

            // Step 5: Run agentic retrieval
            runAgenticRetrieval(credential);

            // Step 6: Clean up
            cleanup(indexClient);

            System.out.println("[DONE] Quickstart completed successfully!");

        } catch (Exception e) {
            System.err.println("[ERROR] Error in main execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createSearchIndex(SearchIndexClient indexClient) {
        System.out.println("[WAIT] Creating search index...");

        try {
            try {
                indexClient.deleteIndex(INDEX_NAME);
                System.out.println("[DELETE] Deleted existing index '" + INDEX_NAME + "'");
            } catch (Exception e) {
                // Index doesn't exist
            }

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
                new SearchField("page_embedding_text_3_large",
                        SearchFieldDataType.collection(SearchFieldDataType.SINGLE))
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

            AzureOpenAIVectorizer vectorizer = new AzureOpenAIVectorizer(
                    "azure_openai_text_3_large")
                .setParameters(new AzureOpenAIVectorizerParameters()
                    .setResourceUrl(AZURE_OPENAI_ENDPOINT)
                    .setDeploymentName(AZURE_OPENAI_EMBEDDING_DEPLOYMENT)
                    .setModelName(AzureOpenAIModelName.TEXT_EMBEDDING_3_LARGE));

            VectorSearch vectorSearch = new VectorSearch()
                .setProfiles(Arrays.asList(
                    new VectorSearchProfile("hnsw_text_3_large", "alg")
                        .setVectorizerName("azure_openai_text_3_large")
                ))
                .setAlgorithms(Arrays.asList(
                    new HnswAlgorithmConfiguration("alg")
                ))
                .setVectorizers(Arrays.asList(vectorizer));

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
            List<SearchDocument> documents = fetchEarthAtNightDocuments();

            searchClient.uploadDocuments(documents);
            System.out.println("[DONE] Uploaded " + documents.size()
                + " documents successfully.");

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

        String documentsUrl = "https://raw.githubusercontent.com/Azure-Samples/"
            + "azure-search-sample-data/refs/heads/main/nasa-e-book/"
            + "earth-at-night-json/documents.json";

        try {
            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(documentsUrl))
                .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request,
                java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("Failed to fetch documents: "
                    + response.statusCode());
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
                    for (JsonNode embedding
                            : doc.get("page_embedding_text_3_large")) {
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

            System.out.println("[DONE] Fetched " + documents.size()
                + " documents from GitHub");
            return documents;

        } catch (Exception e) {
            System.err.println("[ERROR] Error fetching documents: "
                + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void createKnowledgeSource(SearchIndexClient indexClient) {
        System.out.println("[WAIT] Creating knowledge source...");

        try {
            SearchIndexKnowledgeSource knowledgeSource =
                new SearchIndexKnowledgeSource(
                    KNOWLEDGE_SOURCE_NAME,
                    new SearchIndexKnowledgeSourceParameters(INDEX_NAME)
                        .setSourceDataFields(Arrays.asList(
                            new SearchIndexFieldReference("id"),
                            new SearchIndexFieldReference("page_chunk"),
                            new SearchIndexFieldReference("page_number")
                        ))
                );

            indexClient.createOrUpdateKnowledgeSource(knowledgeSource);
            System.out.println("[DONE] Knowledge source '"
                + KNOWLEDGE_SOURCE_NAME + "' created successfully.");

        } catch (Exception e) {
            System.err.println("[ERROR] Error creating knowledge source: "
                + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void createKnowledgeBase(SearchIndexClient indexClient) {
        System.out.println("[WAIT] Creating knowledge base...");

        try {
            KnowledgeBaseAzureOpenAIModel model =
                new KnowledgeBaseAzureOpenAIModel(
                    new AzureOpenAIVectorizerParameters()
                        .setResourceUrl(AZURE_OPENAI_ENDPOINT)
                        .setDeploymentName(AZURE_OPENAI_GPT_DEPLOYMENT)
                        .setModelName(AZURE_OPENAI_GPT_MODEL)
                );

            KnowledgeBase knowledgeBase = new KnowledgeBase(
                    KNOWLEDGE_BASE_NAME,
                    Arrays.asList(new KnowledgeSourceReference(
                        KNOWLEDGE_SOURCE_NAME))
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
            System.out.println("[DONE] Knowledge base '"
                + KNOWLEDGE_BASE_NAME + "' created successfully.");

        } catch (Exception e) {
            System.err.println("[ERROR] Error creating knowledge base: "
                + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void runAgenticRetrieval(TokenCredential credential) {
        System.out.println("[SEARCH] Running agentic retrieval...");

        // Set up messages
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
            "A Q&A agent that can answer questions about the Earth at night.\n"
            + "If you don't have the answer, respond with \"I don't know\".");
        messages.add(systemMessage);

        // Build retrieval client
        KnowledgeBaseRetrievalClient baseClient =
            new KnowledgeBaseRetrievalClientBuilder()
                .endpoint(SEARCH_ENDPOINT)
                .knowledgeBaseName(KNOWLEDGE_BASE_NAME)
                .credential(credential)
                .buildClient();

        // First query
        messages.add(Map.of("role", "user", "content",
            "Why do suburban belts display larger December brightening than "
            + "urban cores even though absolute light levels are higher "
            + "downtown? Why is the Phoenix nighttime street grid is so "
            + "sharply visible from space, whereas large stretches of the "
            + "interstate between midwestern cities remain comparatively "
            + "dim?"));

        KnowledgeBaseRetrievalResult result = retrieve(baseClient, messages);

        String responseText = ((KnowledgeBaseMessageTextContent)
            result.getResponse().get(0).getContent().get(0)).getText();
        messages.add(Map.of("role", "assistant", "content", responseText));

        printResult(responseText, result);

        // Continue conversation
        System.out.println("\n === Continuing Conversation ===");
        System.out.println("[QUESTION] Follow-up question: "
            + "How do I find lava at night?");

        messages.add(Map.of("role", "user", "content",
            "How do I find lava at night?"));

        KnowledgeBaseRetrievalResult followUpResult =
            retrieve(baseClient, messages);

        String followUpResponseText = ((KnowledgeBaseMessageTextContent)
            followUpResult.getResponse().get(0).getContent().get(0)).getText();

        printResult(followUpResponseText, followUpResult);

        System.out.println("\n === Conversation Complete ===");
    }

    private static KnowledgeBaseRetrievalResult retrieve(
            KnowledgeBaseRetrievalClient client,
            List<Map<String, String>> messages) {
        KnowledgeBaseRetrievalRequest request =
            new KnowledgeBaseRetrievalRequest();

        for (Map<String, String> msg : messages) {
            if (!"system".equals(msg.get("role"))) {
                request.getMessages().add(
                    new KnowledgeBaseMessage(Arrays.asList(
                        new KnowledgeBaseMessageTextContent(
                            msg.get("content"))
                    )).setRole(msg.get("role"))
                );
            }
        }
        request.setRetrievalReasoningEffort(
            new KnowledgeRetrievalLowReasoningEffort());

        return client.retrieveFromKnowledgeBase(request);
    }

    private static void printResult(String responseText,
            KnowledgeBaseRetrievalResult result) {
        System.out.println("Response:");
        System.out.println(responseText);

        System.out.println("Activity:");
        for (KnowledgeBaseActivityRecord activity : result.getActivity()) {
            System.out.println("Activity Type: "
                + activity.getClass().getSimpleName());
            System.out.println(activity);
        }

        System.out.println("References:");
        for (KnowledgeBaseReference reference : result.getReferences()) {
            System.out.println("Reference Type: "
                + reference.getClass().getSimpleName());
            System.out.println(reference);
        }
    }

    private static void cleanup(SearchIndexClient indexClient) {
        try {
            indexClient.deleteKnowledgeBase(KNOWLEDGE_BASE_NAME);
            System.out.println("Knowledge base '" + KNOWLEDGE_BASE_NAME
                + "' deleted successfully.");
        } catch (Exception e) {
            System.err.println("[WARN] " + e.getMessage());
        }

        try {
            indexClient.deleteKnowledgeSource(KNOWLEDGE_SOURCE_NAME);
            System.out.println("Knowledge source '" + KNOWLEDGE_SOURCE_NAME
                + "' deleted successfully.");
        } catch (Exception e) {
            System.err.println("[WARN] " + e.getMessage());
        }

        try {
            indexClient.deleteIndex(INDEX_NAME);
            System.out.println("Index '" + INDEX_NAME
                + "' deleted successfully.");
        } catch (Exception e) {
            System.err.println("[WARN] " + e.getMessage());
        }
    }
}
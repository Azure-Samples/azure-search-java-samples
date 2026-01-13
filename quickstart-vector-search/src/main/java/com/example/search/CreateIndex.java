package com.example.search;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.indexes.models.SearchIndex;
import com.azure.search.documents.indexes.models.SearchField;
import com.azure.search.documents.indexes.models.SearchFieldDataType;
import com.azure.search.documents.indexes.models.SearchSuggester;
import com.azure.search.documents.indexes.models.HnswAlgorithmConfiguration;
import com.azure.search.documents.indexes.models.HnswParameters;
import com.azure.search.documents.indexes.models.ExhaustiveKnnAlgorithmConfiguration;
import com.azure.search.documents.indexes.models.ExhaustiveKnnParameters;
import com.azure.search.documents.indexes.models.VectorSearchProfile;
import com.azure.search.documents.indexes.models.VectorSearch;
import com.azure.search.documents.indexes.models.SemanticPrioritizedFields;
import com.azure.search.documents.indexes.models.SemanticField;
import com.azure.search.documents.indexes.models.SemanticSearch;
import com.azure.search.documents.indexes.models.SemanticConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CreateIndex {
    private static final String searchEndpoint;
    private static final String indexName;

    static {
        var properties = new Properties();
        try (var in = CreateIndex.class.getClassLoader()
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

        // Create a SearchIndexClient using DefaultAzureCredential.
        SearchIndexClient searchIndexClient = new SearchIndexClientBuilder()
            .credential(new DefaultAzureCredentialBuilder().build())
            .endpoint(searchEndpoint)
            .buildClient();

        System.out.println("Creating index...");

        // Define fields
        List<SearchField> fields = Arrays.asList(
            new SearchField("HotelId", SearchFieldDataType.STRING)
                .setKey(true)
                .setFilterable(true),
            new SearchField("HotelName", SearchFieldDataType.STRING)
                .setSortable(true)
                .setSearchable(true),
            new SearchField("Description", SearchFieldDataType.STRING)
                .setSearchable(true),
            new SearchField("DescriptionVector",
                SearchFieldDataType.collection(SearchFieldDataType.SINGLE))
                .setSearchable(true)
                .setVectorSearchDimensions(1536)
                .setVectorSearchProfileName("my-vector-profile"),
            new SearchField("Category", SearchFieldDataType.STRING)
                .setSortable(true)
                .setFilterable(true)
                .setFacetable(true)
                .setSearchable(true),
            new SearchField("Tags", SearchFieldDataType.collection(
                SearchFieldDataType.STRING))
                .setSearchable(true)
                .setFilterable(true)
                .setFacetable(true),
            new SearchField("ParkingIncluded", SearchFieldDataType.BOOLEAN)
                .setFilterable(true)
                .setSortable(true)
                .setFacetable(true),
            new SearchField("LastRenovationDate",
                SearchFieldDataType.DATE_TIME_OFFSET)
                .setFilterable(true)
                .setSortable(true)
                .setFacetable(true),
            new SearchField("Rating", SearchFieldDataType.DOUBLE)
                .setFilterable(true)
                .setSortable(true)
                .setFacetable(true),
            new SearchField("Address", SearchFieldDataType.COMPLEX)
                .setFields(Arrays.asList(
                    new SearchField("StreetAddress", SearchFieldDataType.STRING),
                    new SearchField("City", SearchFieldDataType.STRING)
                        .setFilterable(true)
                        .setSortable(true)
                        .setFacetable(true),
                    new SearchField("StateProvince", SearchFieldDataType.STRING)
                        .setFilterable(true)
                        .setFacetable(true),
                    new SearchField("PostalCode", SearchFieldDataType.STRING),
                    new SearchField("Country", SearchFieldDataType.STRING)
                        .setFilterable(true)
                        .setFacetable(true)
                )),
            new SearchField("Location", SearchFieldDataType.GEOGRAPHY_POINT)
                .setFilterable(true)
                .setSortable(true)
        );

        var searchIndex = new SearchIndex(indexName, fields);

        // Define vector search configuration
        var hnswParams = new HnswParameters()
            .setM(16)
            .setEfConstruction(200)
            .setEfSearch(128);
        var hnsw = new HnswAlgorithmConfiguration("hnsw-vector-config");
        hnsw.setParameters(hnswParams);

        var eknnParams = new ExhaustiveKnnParameters();
        var eknn = new ExhaustiveKnnAlgorithmConfiguration("eknn-vector-config");
        eknn.setParameters(eknnParams);

        var vectorProfile = new VectorSearchProfile(
            "my-vector-profile",
            "hnsw-vector-config");
        var vectorSearch = new VectorSearch()
            .setAlgorithms(Arrays.asList(hnsw, eknn))
            .setProfiles(Arrays.asList(vectorProfile));
        searchIndex.setVectorSearch(vectorSearch);

        // Define semantic configuration
        var prioritizedFields = new SemanticPrioritizedFields()
            .setTitleField(new SemanticField("HotelName"))
            .setContentFields(Arrays.asList(new SemanticField("Description")))
            .setKeywordsFields(Arrays.asList(new SemanticField("Category")));
        var semanticConfig = new SemanticConfiguration(
            "semantic-config",
            prioritizedFields);
        var semanticSearch = new SemanticSearch()
            .setConfigurations(Arrays.asList(semanticConfig));
        searchIndex.setSemanticSearch(semanticSearch);

        // Define suggesters
        var suggester = new SearchSuggester("sg", Arrays.asList("HotelName"));
        searchIndex.setSuggesters(Arrays.asList(suggester));

        // Create the search index with the semantic settings.
        SearchIndex result = searchIndexClient.createOrUpdateIndex(searchIndex);
        System.out.println("%s created".formatted(result.getName()));

        System.exit(0);
    }
}

package com.microsoft.azure.search.samples.demo;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchServiceVersion;
import com.azure.search.documents.indexes.SearchIndexAsyncClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.indexes.models.*;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SuggestOptions;
import com.azure.search.documents.util.SearchPagedFlux;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;
import java.util.*;

import static com.microsoft.azure.search.samples.demo.Address.*;
import static com.microsoft.azure.search.samples.demo.Hotel.*;
import static com.microsoft.azure.search.samples.demo.Room.DESCRIPTION;
import static com.microsoft.azure.search.samples.demo.Room.DESCRIPTION_FR;
import static com.microsoft.azure.search.samples.demo.Room.SMOKING_ALLOWED;
import static com.microsoft.azure.search.samples.demo.Room.TAGS;
import static com.microsoft.azure.search.samples.demo.Room.*;

class DemoOperations {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new Jdk8Module());
    private static final String INDEX_NAME = "hotels";
    private final SearchIndexAsyncClient indexAsyncClient;

    DemoOperations(AzureSearchConfig config) {
        indexAsyncClient = new SearchIndexClientBuilder()
                .endpoint(String.format(config.endPoint(), config.serviceName()))
                .credential(new AzureKeyCredential(config.apiKey()))
                .serviceVersion(SearchServiceVersion.getLatest())
                .buildAsyncClient();
    }

    // Indexes may be created via the management UI in portal.azure.com or via APIs. In addition to field
    // details index definitions include options for custom scoring, suggesters and more
    void createIndex() throws IOException, InterruptedException {
        // Typical application initialization may createIndex an index if it doesn't exist. Deleting an index
        // on initialization is a sample-only thing to do
        System.out.println("Confirming that index does not exist (Expect to receive \"Error: No index with the name 'hotels' was found in the index\")");
        SearchIndex index = new SearchIndex(INDEX_NAME);
        index.setFields(Arrays.asList(
                new SearchField(HOTEL_ID, SearchFieldDataType.STRING).setKey(true).setFilterable(true),
                new SearchField(HOTEL_NAME, SearchFieldDataType.STRING).setSearchable(true),
                new SearchField(DESCRIPTION, SearchFieldDataType.STRING).setSearchable(true),
                new SearchField(DESCRIPTION_FR, SearchFieldDataType.STRING).setSearchable(true),
                new SearchField(CATEGORY, SearchFieldDataType.STRING).setSearchable(true).setFilterable(true).setSortable(true).setFacetable(true),
                new SearchField(TAGS, SearchFieldDataType.collection(SearchFieldDataType.STRING)).setSearchable(true).setFilterable(true).setFacetable(true),
                new SearchField(PARKING_INCLUDED, SearchFieldDataType.BOOLEAN).setFilterable(true).setFacetable(true),
                new SearchField(SMOKING_ALLOWED, SearchFieldDataType.BOOLEAN).setFilterable(true).setFacetable(true),
                new SearchField(LAST_RENOVATION_DATE, SearchFieldDataType.DATE_TIME_OFFSET).setFilterable(true).setFacetable(true).setSortable(true),
                new SearchField(RATING, SearchFieldDataType.DOUBLE).setFilterable(true).setSortable(true).setFacetable(true),
                defineAddressField(),
                defineRoomsField()
        ));
        index.setSuggesters(new SearchSuggester("sg", Arrays.asList(HOTEL_NAME)));
        indexAsyncClient.createOrUpdateIndex(index).block();
    }

    private SearchField defineAddressField() {
        return new SearchField("Address", SearchFieldDataType.fromString("Edm.ComplexType")).setFields(
                Arrays.asList(
                        new SearchField(STREET_ADDRESS, SearchFieldDataType.STRING).setSearchable(true),
                        new SearchField(CITY, SearchFieldDataType.STRING).setSearchable(true),
                        new SearchField(STATE, SearchFieldDataType.STRING).setSearchable(true),
                        new SearchField(ZIP_CODE, SearchFieldDataType.STRING).setSearchable(true)
                )
        );
    }

    private SearchField defineRoomsField() {
        return new SearchField("Rooms", SearchFieldDataType.collection(SearchFieldDataType.fromString("Edm.ComplexType"))).setFields(
                Arrays.asList(
                        new SearchField(DESCRIPTION, SearchFieldDataType.STRING).setSearchable(true).setAnalyzerName(LexicalAnalyzerName.EN_LUCENE),
                        new SearchField(DESCRIPTION_FR, SearchFieldDataType.STRING).setSearchable(true).setAnalyzerName(LexicalAnalyzerName.FR_LUCENE),
                        new SearchField(TYPE, SearchFieldDataType.STRING).setSearchable(true),
                        new SearchField(BASE_RATE, SearchFieldDataType.DOUBLE).setFilterable(true).setFacetable(true),
                        new SearchField(BED_OPTIONS, SearchFieldDataType.STRING).setSearchable(true),
                        new SearchField(SLEEPS_COUNT, SearchFieldDataType.INT32).setFilterable(true).setFacetable(true),
                        new SearchField(SMOKING_ALLOWED, SearchFieldDataType.BOOLEAN).setFilterable(true).setFacetable(true),
                        new SearchField(TAGS, SearchFieldDataType.collection(SearchFieldDataType.STRING)).setSearchable(true).setFilterable(true).setFacetable(true)
                )
        );
    }

    void indexData() throws IOException, InterruptedException {
        // In this case we createIndex sample data in-memory. Typically this will come from another database, file or
        // API and will be turned into objects with the desired shape for indexing
        List<Map<String, Object>> ops = new ArrayList<>();
        for (String id : new String[]{"hotel1", "hotel10", "hotel11", "hotel12", "hotel13"}) {
            Hotel hotel = OBJECT_MAPPER.readValue(getClass().getResource("/" + id), Hotel.class);
            ops.add(new ObjectMapper().convertValue(hotel, Map.class));
        }
        indexAsyncClient.getSearchAsyncClient(INDEX_NAME)
                .mergeOrUploadDocuments(ops).block().getResults().forEach(indexingResult -> {
            if (indexingResult.getStatusCode() == 207) {
                System.out.print("handle partial success, check individual client status/error message");
            }
            System.out.printf("Operation for id: %s, success: %s\n", indexingResult.getKey(), indexingResult.getStatusCode());
        });
        ops.clear();
        ops.add(IndexOperation.deleteOperation(HOTEL_ID, "1"));
        indexAsyncClient.getSearchAsyncClient(INDEX_NAME)
                .deleteDocuments(ops).block().getResults().forEach(indexingResult -> {
            if (indexingResult.getStatusCode() == 207) {
                System.out.print("handle partial success, check individual client status/error message");
            }
            System.out.printf("Operation for id: %s, success: %s\n", indexingResult.getKey(), indexingResult.getStatusCode());
        });
    }

    void searchSimple() {

        SearchOptions options =
                new SearchOptions().setIncludeTotalCount(true);
        SearchPagedFlux searchPagedFlux = indexAsyncClient
                .getSearchAsyncClient(INDEX_NAME).search("Lobby", options);
        System.out.printf("Found %s hits\n", searchPagedFlux.getTotalCount().block());
        searchPagedFlux.subscribe(searchResult -> {
            Map<String, Object> resultMap = searchResult.getDocument(Map.class);
            System.out.printf("\tid: %s, name: %s, score: %s\n", resultMap.get(HOTEL_ID),
                    resultMap.get(HOTEL_NAME), searchResult.getScore());
        });
    }

    void searchAllFeatures() {

        SearchOptions options =
                new SearchOptions()
                        .setIncludeTotalCount(true)
                        .setFilter("Rooms/all(r: r/BaseRate lt 260)")
                        .setOrderBy(LAST_RENOVATION_DATE + " desc")
                        .setSelect(HOTEL_ID + "," + DESCRIPTION + "," + LAST_RENOVATION_DATE)
                        .setSearchFields(ROOMS + "/" + DESCRIPTION)
                        .setFacets(TAGS, RATING)
                        .setHighlightFields(HOTEL_NAME)
                        .setHighlightPreTag("*pre*")
                        .setHighlightPostTag("*post*")
                        .setTop(10)
                        .setMinimumCoverage(0.75);
        SearchPagedFlux searchPagedFlux = indexAsyncClient
                .getSearchAsyncClient(INDEX_NAME).search("Mountain", options);
        System.out.printf("Found %s hits, coverage: %s\n", searchPagedFlux.getTotalCount().block(), searchPagedFlux.getCoverage().block());
        searchPagedFlux.getFacets().subscribe(facetResults -> {
            facetResults.keySet().forEach(field -> {
                System.out.println(field + ":");
                if (facetResults.get(field) != null) {
                    facetResults.get(field).forEach(facetResult -> {
                        System.out.printf("\t%s: %s\n", facetResult.getAdditionalProperties().get("value"), facetResult.getCount());
                    });
                } else {
                    System.out.printf("\t%s-%s: %s\n", facetResults.get("from") == null ? "min" : facetResults.get("from"),
                            facetResults.get("to") == null ? "max" : facetResults.get("to"), facetResults.size());
                }
            });
        });
    }

    void lookup() {
        Map<String, Object> hotel = indexAsyncClient.getSearchAsyncClient(INDEX_NAME)
                .getDocument("10", Map.class).block();
        System.out.println("Document lookup, key='10'");
        System.out.printf("\tname: %s\n", hotel.get(HOTEL_NAME));
        System.out.printf("\trenovated: %s\n", hotel.get(LAST_RENOVATION_DATE));
        System.out.printf("\trating: %s\n", hotel.get(RATING));
    }

    void suggest() {
        SuggestOptions options = new SuggestOptions().setUseFuzzyMatching(true);
        System.out.println("Document suggest, suggesterName='sg'");
        indexAsyncClient.getSearchAsyncClient(INDEX_NAME)
                .suggest("res", "sg", options)
                .collectList().block().forEach(suggestResult -> {
            System.out.printf("\ttext: %s (id: %s)\n", suggestResult.getText(), suggestResult.getDocument(Map.class).get(HOTEL_ID));
        });
    }
}

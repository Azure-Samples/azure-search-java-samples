package com.microsoft.azure.search.samples.demo;

import com.azure.search.*;
import com.azure.search.models.*;
import com.azure.search.util.SearchPagedFlux;
import com.azure.search.util.SearchPagedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import reactor.core.publisher.Flux;

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
    private final SearchServiceAsyncClient serviceClient;

    DemoOperations(AzureSearchConfig config) {
        serviceClient = new SearchServiceClientBuilder()
                .endpoint(String.format(config.endPoint(), config.serviceName()))
                .credential(new SearchApiKeyCredential(config.apiKey()))
                .serviceVersion(SearchServiceVersion.getLatest())
                .buildAsyncClient();
    }

    // Indexes may be created via the management UI in portal.azure.com or via APIs. In addition to field
    // details index definitions include options for custom scoring, suggesters and more
    void createIndex() throws IOException, InterruptedException {
        // Typical application initialization may createIndex an index if it doesn't exist. Deleting an index
        // on initialization is a sample-only thing to do
        System.out.println("Confirming that index does not exist (Expect to receive \"Error: No index with the name 'hotels' was found in the index\")");
        Index index = new Index();
        index.setName(INDEX_NAME);
        index.setFields(Arrays.asList(
                new Field().setName(HOTEL_ID).setKey(true).setFilterable(true).setType(DataType.fromString("Edm.String")),
                new Field().setName(HOTEL_NAME).setSearchable(true).setType(DataType.fromString("Edm.String")),
                new Field().setName(DESCRIPTION).setSearchable(true).setType(DataType.fromString("Edm.String")),
                new Field().setName(DESCRIPTION_FR).setSearchable(true).setType(DataType.fromString("Edm.String")),
                new Field().setName(CATEGORY).setSearchable(true).setType(DataType.fromString("Edm.String")),
                new Field().setName(TAGS).setSearchable(true).setFilterable(true).setFacetable(true).setType(DataType.fromString("Collection(Edm.String)")),
                new Field().setName(PARKING_INCLUDED).setFilterable(true).setFacetable(true).setType(DataType.fromString("Edm.Boolean")),
                new Field().setName(SMOKING_ALLOWED).setFilterable(true).setFacetable(true).setType(DataType.fromString("Edm.Boolean")),
                new Field().setName(LAST_RENOVATION_DATE).setFilterable(true).setSortable(true).setFacetable(true).setType(DataType.fromString("Edm.DateTimeOffset")),
                new Field().setName(RATING).setFilterable(true).setFacetable(true).setType(DataType.fromString("Edm.Double")),
                defineAddressField(),
                defineRoomsField()
        ));
        index.setSuggesters(Arrays.asList(new Suggester().setName("sg").setSearchMode("analyzingInfixMatching").setSourceFields(Arrays.asList(HOTEL_NAME))));
        serviceClient.createOrUpdateIndex(index);
    }

    private Field defineAddressField() {
        return new Field().setName("Address").setType(DataType.fromString("Edm.ComplexType")).setFields(
                Arrays.asList(
                        new Field().setName(STREET_ADDRESS).setSearchable(true).setType(DataType.fromString("Edm.String")),
                        new Field().setName(CITY).setSearchable(true).setType(DataType.fromString("Edm.String")),
                        new Field().setName(STATE).setSearchable(true).setType(DataType.fromString("Edm.String")),
                        new Field().setName(ZIP_CODE).setSearchable(true).setType(DataType.fromString("Edm.String"))
                )
        );
    }

    private Field defineRoomsField() {
        return new Field().setName("Rooms").setType(DataType.fromString("Collection(Edm.ComplexType)")).setFields(
                Arrays.asList(
                        new Field().setName(DESCRIPTION).setType(DataType.fromString("Edm.String")).setSearchable(true).setAnalyzer("en.lucene"),
                        new Field().setName(DESCRIPTION_FR).setType(DataType.fromString("Edm.String")).setSearchable(true).setAnalyzer("fr.lucene"),
                        new Field().setName(TYPE).setType(DataType.fromString("Edm.String")).setSearchable(true),
                        new Field().setName(BASE_RATE).setType(DataType.fromString("Edm.Double")).setFilterable(true).setFacetable(true),
                        new Field().setName(BED_OPTIONS).setType(DataType.fromString("Edm.String")).setSearchable(true),
                        new Field().setName(SLEEPS_COUNT).setType(DataType.fromString("Edm.Int32")).setFilterable(true).setFacetable(true),
                        new Field().setName(SMOKING_ALLOWED).setType(DataType.fromString("Edm.Boolean")).setFacetable(true).setFacetable(true),
                        new Field().setName(TAGS).setType(DataType.fromString("Collection(Edm.String)")).setSearchable(true).setFilterable(true).setFacetable(true)
                )
        );
    }

    void indexData() throws IOException {
        // In this case we createIndex sample data in-memory. Typically this will come from another database, file or
        // API and will be turned into objects with the desired shape for indexing
        List<String> ops = new ArrayList<>();
        for (String id : new String[]{"hotel1", "hotel10", "hotel11", "hotel12", "hotel13"}) {
            Hotel hotel = OBJECT_MAPPER.readValue(getClass().getResource("/" + id), Hotel.class);
            ops.add(IndexOperation.uploadOperation(hotel));
        }
        ops.add(IndexOperation.deleteOperation(HOTEL_ID, "1"));

        SearchIndexAsyncClient indexClient = serviceClient.getIndexClient(INDEX_NAME);
        indexClient.mergeOrUploadDocumentsWithResponse(ops).subscribe(response -> {
            if (response.getStatusCode() == 207) {
                System.out.print("handle partial success, check individual client status/error message");
            }
            response.getValue().getResults().forEach(indexingResult -> {
                System.out.printf("Operation for id: %s, success: %s\n", indexingResult.getKey(), indexingResult.getStatusCode());
            });
        });
    }

    void searchSimple() throws IOException {

        SearchIndexAsyncClient indexClient = serviceClient.getIndexClient(INDEX_NAME);
        SearchOptions options =
                new SearchOptions().setIncludeTotalResultCount(true);
        SearchPagedFlux searchPagedFlux =
                indexClient.search("Lobby", options, new RequestOptions());
        System.out.printf("Found %s hits\n", searchPagedFlux.count().block());
        searchPagedFlux.subscribe(searchResult -> {
            System.out.printf("\tid: %s, name: %s, score: %s\n", searchResult.getDocument().get(HOTEL_ID),
                    searchResult.getDocument().get(HOTEL_NAME), searchResult.getScore());
        });
    }

    void searchAllFeatures() throws IOException {

        SearchOptions options =
                new SearchOptions()
                        .setIncludeTotalResultCount(true)
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
        SearchIndexAsyncClient indexClient = serviceClient.getIndexClient(INDEX_NAME);
        SearchPagedFlux searchPagedFlux = indexClient.search(
                "Mountain", options, new RequestOptions());
        int size = searchPagedFlux.collectList().block().size();
        Flux<SearchPagedResponse> pagedResponseFlux = searchPagedFlux.byPage();
        pagedResponseFlux.subscribe(searchPagedResponse -> {
            System.out.printf("Found %s hits, coverage: %s\n", size, searchPagedResponse.getCoverage());
            searchPagedResponse.getValue().forEach(searchResult -> {
                System.out.printf("\tid: %s, name: %s, LastRenovationDate: %s\n", searchResult.getDocument().get(HOTEL_ID),
                        searchResult.getDocument().get(DESCRIPTION), searchResult.getDocument().get(LAST_RENOVATION_DATE));
            });
            searchPagedResponse.getFacets().keySet().forEach(field -> {
                System.out.println(field + ":");
                searchPagedResponse.getFacets().get(field).forEach(facetResult -> {
                    if (facetResult.getAdditionalProperties() != null) {
                        System.out.printf("\t%s: %s\n", facetResult.getAdditionalProperties(), facetResult.getCount());
                    } else {
                        Map<String, Object> additionalProperties = facetResult.getAdditionalProperties();
                        System.out.printf("\t%s-%s: %s\n", additionalProperties.get("form") == null ? "min" : additionalProperties.get("form"),
                                additionalProperties.get("to") == null ? "max" : additionalProperties.get("to"), facetResult.getCount());
                    }
                });
            });
        });
    }

    void lookup() throws IOException {
        SearchIndexAsyncClient indexClient = serviceClient.getIndexClient(INDEX_NAME);
        SearchDocument document = indexClient.getDocument("10").block();
        System.out.println("Document lookup, key='10'");
        System.out.printf("\tname: %s\n", document.get(HOTEL_NAME));
        System.out.printf("\trenovated: %s\n", document.get(LAST_RENOVATION_DATE));
        System.out.printf("\trating: %s\n", document.get(RATING));
    }

    void suggest() throws IOException {
        SearchIndexAsyncClient indexClient = serviceClient.getIndexClient(INDEX_NAME);
        SuggestOptions options = new SuggestOptions().setUseFuzzyMatching(true);
        indexClient.suggest("res", "sg", options, new RequestOptions())
                .byPage().subscribe(suggestPagedResponse -> {
            System.out.println("Suggest results, coverage: " + suggestPagedResponse.getCoverage());
            suggestPagedResponse.getValue().forEach(suggestResult -> {
                System.out.printf("\ttext: %s (id: %s)\n", suggestResult.getText(), suggestResult.getDocument().get(HOTEL_ID));
            });
        });
    }
}

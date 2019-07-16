package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.microsoft.azure.search.samples.client.SearchIndexClient;
import com.microsoft.azure.search.samples.index.ComplexIndexField;
import com.microsoft.azure.search.samples.index.IndexDefinition;
import com.microsoft.azure.search.samples.index.IndexField;
import com.microsoft.azure.search.samples.index.SimpleIndexField;
import com.microsoft.azure.search.samples.index.Suggester;
import com.microsoft.azure.search.samples.options.SearchOptions;
import com.microsoft.azure.search.samples.options.SuggestOptions;
import com.microsoft.azure.search.samples.results.IndexBatchOperationResult;
import com.microsoft.azure.search.samples.results.IndexBatchResult;
import com.microsoft.azure.search.samples.results.SearchResult;
import com.microsoft.azure.search.samples.results.SuggestHit;
import com.microsoft.azure.search.samples.results.SuggestResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.microsoft.azure.search.samples.demo.Address.CITY;
import static com.microsoft.azure.search.samples.demo.Address.STATE;
import static com.microsoft.azure.search.samples.demo.Address.STREET_ADDRESS;
import static com.microsoft.azure.search.samples.demo.Address.ZIP_CODE;
import static com.microsoft.azure.search.samples.demo.Hotel.CATEGORY;
import static com.microsoft.azure.search.samples.demo.Hotel.HOTEL_ID;
import static com.microsoft.azure.search.samples.demo.Hotel.HOTEL_NAME;
import static com.microsoft.azure.search.samples.demo.Hotel.LAST_RENOVATION_DATE;
import static com.microsoft.azure.search.samples.demo.Hotel.PARKING_INCLUDED;
import static com.microsoft.azure.search.samples.demo.Hotel.RATING;
import static com.microsoft.azure.search.samples.demo.Hotel.ROOMS;
import static com.microsoft.azure.search.samples.demo.Room.BASE_RATE;
import static com.microsoft.azure.search.samples.demo.Room.BED_OPTIONS;
import static com.microsoft.azure.search.samples.demo.Room.DESCRIPTION;
import static com.microsoft.azure.search.samples.demo.Room.DESCRIPTION_FR;
import static com.microsoft.azure.search.samples.demo.Room.SLEEPS_COUNT;
import static com.microsoft.azure.search.samples.demo.Room.SMOKING_ALLOWED;
import static com.microsoft.azure.search.samples.demo.Room.TAGS;
import static com.microsoft.azure.search.samples.demo.Room.TYPE;

class DemoOperations {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new Jdk8Module());
    private static final String INDEX_NAME = "hotels";
    private SearchIndexClient client;

    DemoOperations(String serviceName, String apiKey) {
        this.client = new SearchIndexClient(serviceName, INDEX_NAME, apiKey);
    }

    // Indexes may be created via the management UI in portal.azure.com or via APIs. In addition to field
    // details index definitions include options for custom scoring, suggesters and more
    void createIndex() throws IOException {
        // Typical application initialization may createIndex an index if it doesn't exist. Deleting an index
        // on initialization is a sample-only thing to do
        client.deleteIndexIfExists();

        if (!client.doesIndexExist()) {
            List<IndexField> fields =
                    Arrays.asList(SimpleIndexField.builder(HOTEL_ID, "Edm.String")
                                          .key(true).filterable(true).build(),
                                  SimpleIndexField.builder(HOTEL_NAME, "Edm.String")
                                          .searchable(true).build(),
                                  SimpleIndexField.builder(DESCRIPTION, "Edm.String")
                                          .searchable(true).build(),
                                  SimpleIndexField.builder(DESCRIPTION_FR, "Edm.String")
                                          .searchable(true).analyzer("fr.lucene").build(),
                                  SimpleIndexField.builder(CATEGORY, "Edm.String")
                                          .searchable(true).filterable(true).sortable(true).facetable(true).build(),
                                  SimpleIndexField.builder(TAGS, "Collection(Edm.String)")
                                          .searchable(true).filterable(true).facetable(true).build(),
                                  SimpleIndexField.builder(PARKING_INCLUDED, "Edm.Boolean")
                                          .filterable(true).facetable(true).build(),
                                  SimpleIndexField.builder(SMOKING_ALLOWED, "Edm.Boolean")
                                          .filterable(true).facetable(true).build(),
                                  SimpleIndexField.builder(LAST_RENOVATION_DATE, "Edm.DateTimeOffset")
                                          .filterable(true).sortable(true).facetable(true).build(),
                                  SimpleIndexField.builder(RATING, "Edm.Double")
                                          .filterable(true).sortable(true).facetable(true).build(),
                                  defineAddressField(),
                                  defineRoomsField());

            Suggester suggester = Suggester.create("sg", "analyzingInfixMatching",
                                                   Collections.singletonList(HOTEL_NAME));

            client.createIndex(IndexDefinition.create(INDEX_NAME, fields, Collections.singletonList(suggester)));
        }
    }

    private ComplexIndexField defineAddressField() {
        return ComplexIndexField
                .create("Address",
                        Arrays.asList(
                                SimpleIndexField
                                        .builder(STREET_ADDRESS, "Edm.String")
                                        .searchable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(CITY, "Edm.String")
                                        .searchable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(STATE, "Edm.String")
                                        .searchable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(ZIP_CODE, "Edm.String")
                                        .searchable(true)
                                        .build()
                        ),
                        false);
    }

    private ComplexIndexField defineRoomsField() {
        return ComplexIndexField
                .create("Rooms",
                        Arrays.asList(
                                SimpleIndexField
                                        .builder(DESCRIPTION, "Edm.String")
                                        .searchable(true)
                                        .analyzer("en.lucene")
                                        .build(),
                                SimpleIndexField
                                        .builder(DESCRIPTION_FR, "Edm.String")
                                        .searchable(true)
                                        .analyzer("fr.lucene")
                                        .build(),
                                SimpleIndexField
                                        .builder(TYPE, "Edm.String")
                                        .searchable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(BASE_RATE, "Edm.Double")
                                        .filterable(true)
                                        .facetable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(BED_OPTIONS, "Edm.String")
                                        .searchable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(SLEEPS_COUNT, "Edm.Int32")
                                        .filterable(true)
                                        .facetable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(SMOKING_ALLOWED, "Edm.Boolean")
                                        .filterable(true)
                                        .facetable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(TAGS, "Collection(Edm.String)")
                                        .searchable(true)
                                        .filterable(true)
                                        .facetable(true)
                                        .build()
                        ),
                        true);
    }

    void indexData() throws IOException {
        // In this case we createIndex sample data in-memory. Typically this will come from another database, file or
        // API and will be turned into objects with the desired shape for indexing
        List<IndexOperation> ops = new ArrayList<>();
        for (String id : new String[] { "hotel1", "hotel10","hotel11", "hotel12", "hotel13"}) {
            Hotel hotel = OBJECT_MAPPER.readValue(getClass().getResource("/" + id), Hotel.class);
            ops.add(IndexOperation.uploadOperation(hotel));
        }
        ops.add(IndexOperation.deleteOperation(HOTEL_ID, "1"));

        IndexBatchResult result = client.indexBatch(ops);
        if (result.status() != null && result.status() ==  207) {
            System.out.print("handle partial success, check individual client status/error message");
        }
        for (IndexBatchOperationResult r : result.value()) {
            System.out.printf("Operation for id: %s, success: %s\n", r.key(), r.status());
        }
    }

    void searchSimple() throws IOException {
        SearchOptions options = SearchOptions.builder().includeCount(true).build();
        SearchResult result = client.search("Lobby", options);
        System.out.printf("Found %s hits\n", result.count());
        for (SearchResult.SearchHit hit : result.hits()) {
            System.out.printf("\tid: %s, name: %s, score: %s\n", hit.document().get(HOTEL_ID),
                              hit.document().get(HOTEL_NAME), hit.score());
        }
    }

    void searchAllFeatures() throws IOException {
        SearchOptions options = SearchOptions.builder()
                .includeCount(true)
                .filter("Rooms/all(r: r/BaseRate lt 260)")
                .orderBy(LAST_RENOVATION_DATE + " desc")
                .select(HOTEL_ID + "," + DESCRIPTION + "," + LAST_RENOVATION_DATE)
                .searchFields(ROOMS + "/" + DESCRIPTION)
                .facets(Arrays.asList(TAGS, RATING))
                .highlight(HOTEL_NAME)
                .highlightPreTag("*pre*")
                .highlightPostTag("*post*")
                .top(10)
                .requireAllTerms(true)
                .minimumCoverage(0.75).build();

        SearchResult result = client.search("Mountain", options);

        // list search hits
        System.out.printf("Found %s hits, coverage: %s\n", result.count(),
                          result.coverage() == null ? "-" : result.coverage());
        for (SearchResult.SearchHit hit : result.hits()) {
            System.out.printf("\tid: %s, name: %s, LastRenovationDate: %s\n", hit.document().get(HOTEL_ID),
                              hit.document().get(DESCRIPTION), hit.document().get(LAST_RENOVATION_DATE));
        }

        // list facets
        for (String field : Objects.requireNonNull(result.facets()).keySet()) {
            System.out.println(field + ":");
            for (SearchResult.FacetValue value : Objects.requireNonNull(result.facets()).get(field)) {
                if (value.value() != null) {
                    System.out.printf("\t%s: %s\n", value.value(), value.count());
                } else {
                    System.out.printf("\t%s-%s: %s\n", value.from() == null ? "min" : value.from(),
                                      value.to() == null ? "max" : value.to(), value.count());
                }
            }
        }
    }

    void lookup() throws IOException {
        Map<String, Object> document = client.lookup("10");
        System.out.println("Document lookup, key='10'");
        System.out.printf("\tname: %s\n", document.get(HOTEL_NAME));
        System.out.printf("\trenovated: %s\n", document.get(LAST_RENOVATION_DATE));
        System.out.printf("\trating: %s\n", document.get(RATING));
    }

    void suggest() throws IOException {
        SuggestOptions options = SuggestOptions.builder().fuzzy(true).build();
        SuggestResult result = client.suggest("res", "sg", options);
        System.out.println("Suggest results, coverage: " + result.coverage());
        for (SuggestHit hit : result.hits()) {
            System.out.printf("\ttext: %s (id: %s)\n", hit.text(), hit.document().get(HOTEL_ID));
        }
    }
}

package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.microsoft.azure.search.samples.client.SearchIndexClient;
import com.microsoft.azure.search.samples.index.ComplexIndexField;
import com.microsoft.azure.search.samples.index.IndexDefinition;
import com.microsoft.azure.search.samples.index.IndexField;
import com.microsoft.azure.search.samples.index.SimpleIndexField;
import com.microsoft.azure.search.samples.options.SearchOptions;
import com.microsoft.azure.search.samples.results.IndexBatchOperationResult;
import com.microsoft.azure.search.samples.results.IndexBatchResult;
import com.microsoft.azure.search.samples.results.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.microsoft.azure.search.samples.demo.Address.CITY;
import static com.microsoft.azure.search.samples.demo.Address.STATE_PROVINCE;
import static com.microsoft.azure.search.samples.demo.Address.STREET_ADDRESS;
import static com.microsoft.azure.search.samples.demo.Address.POSTAL_CODE;
import static com.microsoft.azure.search.samples.demo.Hotel.*;


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
        System.out.printf("\nCreating the index");
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
                                    .searchable(true).filterable(true).sortable(true).build(),
                            SimpleIndexField.builder(TAGS, "Collection(Edm.String)")
                                    .searchable(true).filterable(true).build(),
                            SimpleIndexField.builder(PARKING_INCLUDED, "Edm.Boolean")
                                    .filterable(true).build(),
                            SimpleIndexField.builder(LAST_RENOVATION_DATE, "Edm.DateTimeOffset")
                                    .filterable(true).sortable(true).build(),
                            SimpleIndexField.builder(RATING, "Edm.Double")
                                    .filterable(true).sortable(true).build(),
                            defineAddressField());
            client.createIndex(IndexDefinition.create(INDEX_NAME, fields));
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
                                        .builder(STATE_PROVINCE, "Edm.String")
                                        .searchable(true)
                                        .build(),
                                SimpleIndexField
                                        .builder(POSTAL_CODE, "Edm.String")
                                        .searchable(true)
                                        .build()
                        ),
                        false);
    }

    void indexData() throws IOException {
        // In this case we createIndex sample data in-memory. Typically this will come from another database, file or
        // API and will be turned into objects with the desired shape for indexing

        System.out.printf("\nIndexing the data\n");

        List<IndexOperation> ops = new ArrayList<>();
        for (String id : new String[] {"hotel1","hotel2","hotel3","hotel4"}) {
            Hotel hotel = OBJECT_MAPPER.readValue(getClass().getResource("/" + id), Hotel.class);
            ops.add(IndexOperation.uploadOperation(hotel));
        }

        IndexBatchResult result = client.indexBatch(ops);
        if (result.status() != null && result.status() ==  207) {
            System.out.print("handle partial success, check individual client status/error message");
        }
        for (IndexBatchOperationResult r : result.value()) {
            System.out.printf("Hotel id: %s indexed successfully? %s\n", r.key(), r.status());
        }
    }


    void searchAllHotels() throws IOException {
        SearchOptions options = SearchOptions.builder().includeCount(true).build();
        SearchResult result = client.search("*", options);

        //list search results
        System.out.printf("\nSearch for: All hotels\n");
        System.out.printf("Return: The hotel ID and name\n");
        System.out.printf("Query string: &search=*&$select=HotelId,HotelName\n");
        System.out.printf("Search results:\n");
        for (SearchResult.SearchHit hit : result.hits()) {
            System.out.printf("\tid: %s, name: %s\n", hit.document().get(HOTEL_ID),
                    hit.document().get(HOTEL_NAME));
        }
    }

    //Search for hotels with a restaurant and wifi
    //Return the hotel name, description and tags
    void searchTwoTerms() throws IOException {
        SearchOptions options = SearchOptions.builder()
                .select(HOTEL_NAME + "," + DESCRIPTION + "," + TAGS).build();

        SearchResult result = client.search("restaurant wifi", options);

        // list search results
        System.out.printf("\nSearch for: Hotels with a restaurant and wifi\n");
        System.out.printf("Return: The hotel name and attributes (tags)\n");
        System.out.printf("Query string: &search=restaurant wifi&$select=HotelName,Description,Tags\n");
        System.out.printf("Search results:\n");
        for (SearchResult.SearchHit hit : result.hits()) {
            System.out.printf("\tName: %s, Description: %s\n", hit.document().get(HOTEL_NAME),
                    hit.document().get(DESCRIPTION));
            System.out.printf("\tTags: %s\n",  hit.document().get(TAGS));

        }

    }

    void searchWithFilter() throws IOException {
        SearchOptions options = SearchOptions.builder()
                .select(HOTEL_NAME + "," + RATING)
                .filter(RATING + " gt 4").build();

        SearchResult result = client.search("*", options);

        // list search results
        System.out.printf("\nSearch for: Hotels with a rating greater than 4\n");
        System.out.printf("Return: The hotel name and rating\n");
        System.out.printf("Query string: &search=*&$filter=Rating gt 4&$select= HotelName, Rating\n");
        System.out.printf("Search results:\n");
        for (SearchResult.SearchHit hit : result.hits()) {
            System.out.printf("\tName: %s, Rating: %s\n", hit.document().get(HOTEL_NAME),
                    hit.document().get(RATING));
        }
    }
    void searchTopTwo() throws IOException {
        SearchOptions options = SearchOptions.builder()
                .select(HOTEL_NAME + "," + CATEGORY)
                .top(2).build();

        SearchResult result = client.search("boutique", options);

        // list search results
        System.out.printf("\nSearch for: Boutique hotels\n");
        System.out.printf("Return: The hotel name and category\n");
        System.out.printf("Query string: &search=boutique&$top=2&$select=HotelName, Category'\n");
        System.out.printf("Search results:\n");
        for (SearchResult.SearchHit hit : result.hits()) {
            System.out.printf("\t Name: %s, Category: %s\n", hit.document().get(HOTEL_NAME),
                    hit.document().get(CATEGORY));
        }
    }

    void searchOrderResults() throws IOException {
        SearchOptions options = SearchOptions.builder()
                .select(HOTEL_NAME + "," + RATING + "," + TAGS)
                .orderBy(RATING).build();

        SearchResult result = client.search("pool", options);

        // list search results
        System.out.printf("\nSearch for: Top 2 hotels with a pool, ordered by rating\n");
        System.out.printf("Return: The hotel name and rating\n");
        System.out.printf("Query string: &search=pool&$orderBy=Rating&$select=HotelName,Rating\n");
        System.out.printf("Search results:\n");
        for (SearchResult.SearchHit hit : result.hits()) {
            System.out.printf("\tName: %s, Rating: %s\n", hit.document().get(HOTEL_NAME),
                    hit.document().get(RATING));
        }
    }


}


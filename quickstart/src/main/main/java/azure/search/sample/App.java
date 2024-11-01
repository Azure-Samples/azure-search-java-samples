// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package azure.search.sample;

import java.util.Arrays;
import java.util.ArrayList;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.Context;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.indexes.models.IndexDocumentsBatch;
import com.azure.search.documents.indexes.models.SearchIndex;
import com.azure.search.documents.indexes.models.SearchSuggester;
import com.azure.search.documents.util.AutocompletePagedIterable;
import com.azure.search.documents.util.SearchPagedIterable;

public class App {

    public static void main(String[] args) {
        // Connection to your search service.
        // Provide your service name and a valid admin API key.
        var searchServiceEndpoint = "https://<your-service>.search.windows.net";
        var adminKey = new AzureKeyCredential("<your-admin-key>");
        String indexName = "hotels-java-demo";

        SearchIndexClient searchIndexClient = new SearchIndexClientBuilder()
            .endpoint(searchServiceEndpoint)
            .credential(adminKey)
            .buildClient();

        SearchClient searchClient = new SearchClientBuilder()
            .endpoint(searchServiceEndpoint)
            .credential(adminKey)
            .indexName(indexName)
            .buildClient();

        // Create Search Index for Hotel model
        searchIndexClient.createOrUpdateIndex(
            new SearchIndex(indexName, SearchIndexClient.buildSearchFields(Hotel.class, null))
            .setSuggesters(new SearchSuggester("sg", Arrays.asList("HotelName"))));

        // Upload sample hotel documents to the Search Index
        uploadDocuments(searchClient);

        // Wait 2 seconds for indexing to complete before starting queries (for demo and console-app purposes only)
        System.out.println("Waiting for indexing...\n");
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
        }

        // Call the RunQueries method to invoke a series of queries
        System.out.println("Starting queries...\n");
        RunQueries(searchClient);

        // End the program
        System.out.println("Complete.\n");
    }

    // Upload documents in a single Upload request.
    private static void uploadDocuments(SearchClient searchClient)
    {
        var hotelList = new ArrayList<Hotel>();

        var hotel = new Hotel();
        hotel.hotelId = "1";
        hotel.hotelName = "Stay-Kay City Hotel";
        hotel.description = "The hotel is ideally located on the main commercial artery of the city in the heart of New York. A few minutes away is Time's Square and the historic centre of the city, as well as other places of interest that make New York one of America's most attractive and cosmopolitan cities.";
        hotel.descriptionFr = "L'hôtel est idéalement situé sur la principale artère commerciale de la ville en plein cœur de New York. A quelques minutes se trouve la place du temps et le centre historique de la ville, ainsi que d'autres lieux d'intérêt qui font de New York l'une des villes les plus attractives et cosmopolites de l'Amérique.";
        hotel.category = "Boutique";
        hotel.tags = new String[] { "pool", "air conditioning", "concierge" };
        hotel.parkingIncluded = false;
        hotel.lastRenovationDate = OffsetDateTime.of(LocalDateTime.of(LocalDate.of(1970, 1, 18), LocalTime.of(0, 0)), ZoneOffset.UTC);
        hotel.rating = 3.6;
        hotel.address = new Address();
        hotel.address.streetAddress = "677 5th Ave";
        hotel.address.city = "New York";
        hotel.address.stateProvince = "NY";
        hotel.address.postalCode = "10022";
        hotel.address.country = "USA";
        hotelList.add(hotel);

        hotel = new Hotel();
        hotel.hotelId = "2";
        hotel.hotelName = "Old Century Hotel";
        hotel.description = "The hotel is situated in a  nineteenth century plaza, which has been expanded and renovated to the highest architectural standards to create a modern, functional and first-class hotel in which art and unique historical elements coexist with the most modern comforts.";
        hotel.descriptionFr = "L'hôtel est situé dans une place du XIXe siècle, qui a été agrandie et rénovée aux plus hautes normes architecturales pour créer un hôtel moderne, fonctionnel et de première classe dans lequel l'art et les éléments historiques uniques coexistent avec le confort le plus moderne.";
        hotel.category = "Boutique";
        hotel.tags = new String[] { "pool", "free wifi", "concierge" };
        hotel.parkingIncluded = false;
        hotel.lastRenovationDate = OffsetDateTime.of(LocalDateTime.of(LocalDate.of(1979, 2, 18), LocalTime.of(0, 0)), ZoneOffset.UTC);
        hotel.rating = 3.60;
        hotel.address = new Address();
        hotel.address.streetAddress = "140 University Town Center Dr";
        hotel.address.city = "Sarasota";
        hotel.address.stateProvince = "FL";
        hotel.address.postalCode = "34243";
        hotel.address.country = "USA";
        hotelList.add(hotel);

        hotel = new Hotel();
        hotel.hotelId = "3";
        hotel.hotelName = "Gastronomic Landscape Hotel";
        hotel.description = "The Hotel stands out for its gastronomic excellence under the management of William Dough, who advises on and oversees all of the Hotel’s restaurant services.";
        hotel.descriptionFr = "L'hôtel est situé dans une place du XIXe siècle, qui a été agrandie et rénovée aux plus hautes normes architecturales pour créer un hôtel moderne, fonctionnel et de première classe dans lequel l'art et les éléments historiques uniques coexistent avec le confort le plus moderne.";
        hotel.category = "Resort and Spa";
        hotel.tags = new String[] { "air conditioning", "bar", "continental breakfast" };
        hotel.parkingIncluded = true;
        hotel.lastRenovationDate = OffsetDateTime.of(LocalDateTime.of(LocalDate.of(2015, 9, 20), LocalTime.of(0, 0)), ZoneOffset.UTC);
        hotel.rating = 4.80;
        hotel.address = new Address();
        hotel.address.streetAddress = "3393 Peachtree Rd";
        hotel.address.city = "Atlanta";
        hotel.address.stateProvince = "GA";
        hotel.address.postalCode = "30326";
        hotel.address.country = "USA";
        hotelList.add(hotel);

        hotel = new Hotel();
        hotel.hotelId = "4";
        hotel.hotelName = "Sublime Palace Hotel";
        hotel.description = "Sublime Palace  Hotel is located in the heart of the historic center of Sublime in an extremely vibrant and lively area within short walking distance to the sites and landmarks of the city and is surrounded by the extraordinary beauty of churches, buildings, shops and monuments. Sublime Palace is part of a lovingly restored 1800 palace.";
        hotel.descriptionFr = "Le Sublime Palace Hotel est situé au coeur du centre historique de sublime dans un quartier extrêmement animé et vivant, à courte distance de marche des sites et monuments de la ville et est entouré par l'extraordinaire beauté des églises, des bâtiments, des commerces et Monuments. Sublime Palace fait partie d'un Palace 1800 restauré avec amour.";
        hotel.category = "Boutique";
        hotel.tags = new String[] { "concierge", "view", "24-hour front desk service" };
        hotel.parkingIncluded = true;
        hotel.lastRenovationDate = OffsetDateTime.of(LocalDateTime.of(LocalDate.of(1960, 2, 06), LocalTime.of(0, 0)), ZoneOffset.UTC);
        hotel.rating = 4.60;
        hotel.address = new Address();
        hotel.address.streetAddress = "7400 San Pedro Ave";
        hotel.address.city = "San Antonio";
        hotel.address.stateProvince = "TX";
        hotel.address.postalCode = "78216";
        hotel.address.country = "USA";
        hotelList.add(hotel);

        var batch = new IndexDocumentsBatch<Hotel>();
        batch.addMergeOrUploadActions(hotelList);
        try
        {
            searchClient.indexDocuments(batch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // If for some reason any documents are dropped during indexing, you can compensate by delaying and
            // retrying. This simple demo just logs failure and continues
            System.err.println("Failed to index some of the documents");
        }
    }

    // Write search results to console
    private static void WriteSearchResults(SearchPagedIterable searchResults)
    {
        searchResults.iterator().forEachRemaining(result ->
        {
            Hotel hotel = result.getDocument(Hotel.class);
            System.out.println(hotel);
        });

        System.out.println();
    }

    // Write autocomplete results to console
    private static void WriteAutocompleteResults(AutocompletePagedIterable autocompleteResults)
    {
        autocompleteResults.iterator().forEachRemaining(result ->
        {
            String text = result.getText();
            System.out.println(text);
        });

        System.out.println();
    }

    // Run queries, use WriteDocuments to print output
    private static void RunQueries(SearchClient searchClient)
    {
        // Query 1
        System.out.println("Query #1: Search on empty term '*' to return all documents, showing a subset of fields...\n");

        SearchOptions options = new SearchOptions();
        options.setIncludeTotalCount(true);
        options.setFilter("");
        options.setOrderBy("");
        options.setSelect("HotelId", "HotelName", "Address/City");

        WriteSearchResults(searchClient.search("*", options, Context.NONE));

        // Query 2
        System.out.println("Query #2: Search on 'hotels', filter on 'Rating gt 4', sort by Rating in descending order...\n");

        options = new SearchOptions();
        options.setFilter("Rating gt 4");
        options.setOrderBy("Rating desc");
        options.setSelect("HotelId", "HotelName", "Rating");

        WriteSearchResults(searchClient.search("hotels", options, Context.NONE));

        // Query 3
        System.out.println("Query #3: Limit search to specific fields (pool in Tags field)...\n");

        options = new SearchOptions();
        options.setSearchFields("Tags");

        options.setSelect("HotelId", "HotelName", "Tags");

        WriteSearchResults(searchClient.search("pool", options, Context.NONE));

        // Query 4
        System.out.println("Query #4: Facet on 'Category'...\n");

        options = new SearchOptions();
        options.setFilter("");
        options.setFacets("Category");
        options.setSelect("HotelId", "HotelName", "Category");

        WriteSearchResults(searchClient.search("*", options, Context.NONE));

        // Query 5
        System.out.println("Query #5: Look up a specific document...\n");

        Hotel lookupResponse = searchClient.getDocument("3", Hotel.class);
        System.out.println(lookupResponse.hotelId);
        System.out.println();

         // Query 6
        System.out.println("Query #6: Call Autocomplete on HotelName that starts with 's'...\n");

        WriteAutocompleteResults(searchClient.autocomplete("s", "sg"));
    }
}

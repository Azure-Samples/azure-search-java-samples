import com.microsoft.azure.search.samples.client.SearchServiceHelper;
import com.microsoft.azure.search.samples.demo.AzureSearchConfig;
import com.microsoft.azure.search.samples.options.SearchOptions;
import com.microsoft.azure.search.samples.options.SuggestOptions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchServiceHelperTests {
    AzureSearchConfig config = AzureSearchConfig.create("serviceName", "apiKey", "hotels", "hotel-indexer", "2019-05-06", "hotels-datasource");

    @Test
    void indexUrl() {
        assertEquals(URI.create("https://serviceName.search.windows.net/indexes/hotels?api-version=2019-05-06"), SearchServiceHelper.getIndexUrl(config));
    }

    @Test
    void indexLookupUrl() {
        assertEquals(URI.create("https://serviceName.search.windows.net/indexes/hotels/docs('hotelID')?api-version=2019-05-06"), SearchServiceHelper.getIndexLookupUrl(config, "hotelID"));
    }

    @Test
    void indexing() {
        assertEquals(URI.create("https://serviceName.search.windows.net/indexes/hotels/docs/index?api-version=2019-05-06"), SearchServiceHelper.getIndexingUrl(config));
    }

    @Test
    void buildSearch() {
        assertEquals(URI.create("https://servicename.search.windows.net/indexes/hotels/docs?api-version=2019-05-06&search=searchTerm"), SearchServiceHelper.buildSearchUrl(config, "searchTerm", SearchOptions.builder().build()));
        var so = SearchOptions.builder()
                .minimumCoverage(12.5)
                .requireAllTerms(true)
                .skip(4)
                .top(5)
                .highlightPostTag("</b>")
                .highlight("beach")
                .highlightPreTag("<b>")
                .searchFields("A,B")
                .select("Description,BaseRate")
                .orderBy("BaseRate")
                .filter("baseRate lt 150")
                .facets(Arrays.asList("Facet1", "Facet2"))
                .includeCount(true)
                .scoringParameters(Collections.singletonList("SP1,SP2"))
                .scoringProfile("geo")
                .build();
        var longQueryParams = "&$filter=baseRate+lt+150&$orderBy=BaseRate&$select=Description%2CBaseRate&searchFields=A%2CB&highlight=beach&highlightPreTag=%3Cb%3E&highlightPostTag=%3C%2Fb%3E&scoringProfile=geo&$top=5&$skip=4&minimumCoverage=12.5&facet=Facet1&facet=Facet2&scoringParameter=SP1%2CSP2&searchMode=all&$count=true";
        var longURI = "https://servicename.search.windows.net/indexes/hotels/docs?api-version=2019-05-06&search=searchTerm" + longQueryParams;
        assertEquals(URI.create(longURI), SearchServiceHelper.buildSearchUrl(config, "searchTerm", so));
    }

    @Test
    void buildSuggest() {
        var simpleSuggest = SearchServiceHelper.buildIndexSuggestUrl(config, "searchTerm", "suggesterName", SuggestOptions.builder().build());
        assertEquals(URI.create("https://servicename.search.windows.net/indexes/hotels/docs/suggest?api-version=2019-05-06&search=searchTerm&suggesterName=suggesterName"), simpleSuggest);

        var so = SuggestOptions.builder()
                .minimumCoverage(12.5)
                .top(5)
                .highlightPostTag("</b>")
                .highlightPreTag("<b>")
                .top(10)
                .minimumCoverage(3.4)
                .fuzzy(true)
                .searchFields("A,B")
                .select("Description,BaseRate")
                .filter("baseRate lt 150")
                .build();
        var longQueryParams = "&$filter=baseRate+lt+150&$select=Description%2CBaseRate&searchFields=A%2CB&highlightPreTag=%3Cb%3E&highlightPostTag=%3C%2Fb%3E&$top=10&minimumCoverage=3.4&fuzzy=true";
        var longURI = "https://servicename.search.windows.net/indexes/hotels/docs/suggest?api-version=2019-05-06&search=searchTerm&suggesterName=suggesterName" + longQueryParams;
        assertEquals(URI.create(longURI), SearchServiceHelper.buildIndexSuggestUrl(config, "searchTerm", "suggesterName", so));
    }
}

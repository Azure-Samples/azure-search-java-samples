import com.microsoft.azure.search.samples.options.SearchOptions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SearchOptionsTests {

    @Test
    void emptyOptionsMeansEmptyQueryParams() {
        var empty = SearchOptions.builder().build();
        var s = empty.toQueryParameters();
        assertEquals("", s);
    }

    @Test
    void includeCountWorks() {
        var cTrue = SearchOptions.builder().includeCount(true).build();
        var cFalse = SearchOptions.builder().includeCount(false).build();
        assertEquals("&$count=true", cTrue.toQueryParameters());
        assertEquals ("&$count=false", cFalse.toQueryParameters());
    }

    @Test
    void filterWorks() {
        var so = SearchOptions.builder().filter("baseRate lt 150").build();
        assertEquals("&$filter=baseRate+lt+150", so.toQueryParameters());
    }

    @Test
    void orderByWorks() {
        var so = SearchOptions.builder().orderBy("baseRate").build();
        assertEquals("&$orderBy=baseRate", so.toQueryParameters());
    }

    @Test
    void selectWorks() {
        var so = SearchOptions.builder().select("listingId, street, status, daysOnMarket, description").build();
        assertEquals("&$select=listingId%2C+street%2C+status%2C+daysOnMarket%2C+description", so.toQueryParameters());
    }

    @Test
    void searchFieldsWorks() {
        var so = SearchOptions.builder().searchFields("A,B").build();
        assertEquals("&searchFields=A%2CB", so.toQueryParameters());
    }

    @Test
    void facetsWorks() {
        var soEmpty = SearchOptions.builder().facets(new ArrayList<String>()).build();
        var single = Arrays.asList("A");
        var soSingle = SearchOptions.builder().facets(single).build();
        var multi = Arrays.asList("A","B","C");
        var soMulti = SearchOptions.builder().facets(multi).build();
        assertEquals("", soEmpty.toQueryParameters());
        assertEquals("&facet=A",soSingle.toQueryParameters());
        assertEquals("&facet=A&facet=B&facet=C", soMulti.toQueryParameters());
    }

    @Test
    void highlightWorks() {
        var so = SearchOptions.builder().highlight("beach").build();
        assertEquals("&highlight=beach", so.toQueryParameters());
    }

    @Test
    void highlightPreAndPostWork() {
        var so = SearchOptions.builder().highlightPreTag("<em>").highlightPostTag("</em>").build();
        assertEquals("&highlightPreTag=%3Cem%3E&highlightPostTag=%3C%2Fem%3E", so.toQueryParameters());
    }

    @Test
    void scoringProfileWorks() {
        var so = SearchOptions.builder().scoringProfile("geo").build();
        assertEquals("&scoringProfile=geo", so.toQueryParameters());
    }

    @Test
    void topWorks() {
        var so = SearchOptions.builder().top(10).build();
        assertEquals("&$top=10", so.toQueryParameters());
    }

    @Test
    void skipWorks() {
        var so = SearchOptions.builder().skip(10).build();
        assertEquals("&$skip=10", so.toQueryParameters());
    }

    @Test
    void requireAllTermsWorks() {
        var so = SearchOptions.builder().requireAllTerms(true).build();
        assertEquals("&searchMode=all", so.toQueryParameters());
    }

    @Test
    void minimumCoverageWorks() {
        var so = SearchOptions.builder().minimumCoverage(12.5).build();
        assertEquals("&minimumCoverage=12.5", so.toQueryParameters());
    }

    @Test
    void allCompositionWorks() {
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
                .scoringParameters(Arrays.asList("SP1,SP2"))
                .scoringProfile("geo")
                .build();
        var longQueryParams = "&$filter=baseRate+lt+150&$orderBy=BaseRate&$select=Description%2CBaseRate&searchFields=A%2CB&highlight=beach&highlightPreTag=%3Cb%3E&highlightPostTag=%3C%2Fb%3E&scoringProfile=geo&$top=5&$skip=4&minimumCoverage=12.5&facet=Facet1&facet=Facet2&scoringParameter=SP1%2CSP2&searchMode=all&$count=true";
        assertEquals(longQueryParams, so.toQueryParameters());
    }
}

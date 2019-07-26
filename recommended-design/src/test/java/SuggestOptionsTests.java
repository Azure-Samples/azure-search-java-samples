import com.microsoft.azure.search.samples.options.SuggestOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SuggestOptionsTests {
    @Test
    void emptyOptionsMeansEmptyQueryParams() {
        var empty = SuggestOptions.builder().build();
        var s = empty.toQueryParameters();
        assertEquals("", s);
    }

    @Test
    void filterWorks() {
        var so = SuggestOptions.builder().filter("baseRate lt 150").build();
        assertEquals("&$filter=baseRate+lt+150", so.toQueryParameters());
    }

    @Test
    void orderByWorks() {
        var so = SuggestOptions.builder().orderBy("baseRate").build();
        assertEquals("&$orderBy=baseRate", so.toQueryParameters());
    }

    @Test
    void selectWorks() {
        var so = SuggestOptions.builder().select("listingId, street, status, daysOnMarket, description").build();
        assertEquals("&$select=listingId%2C+street%2C+status%2C+daysOnMarket%2C+description", so.toQueryParameters());
    }

    @Test
    void searchFieldsWorks() {
        var so = SuggestOptions.builder().searchFields("A,B").build();
        assertEquals("&searchFields=A%2CB", so.toQueryParameters());
    }


    @Test
    void highlightPreAndPostWork() {
        var so = SuggestOptions.builder().highlightPreTag("<em>").highlightPostTag("</em>").build();
        assertEquals("&highlightPreTag=%3Cem%3E&highlightPostTag=%3C%2Fem%3E", so.toQueryParameters());
    }

    @Test
    void topWorks() {
        var so = SuggestOptions.builder().top(10).build();
        assertEquals("&$top=10", so.toQueryParameters());
    }

    @Test
    void minimumCoverageWorks() {
        var so = SuggestOptions.builder().minimumCoverage(12.5).build();
        assertEquals("&minimumCoverage=12.5", so.toQueryParameters());
    }

    @Test
    void fuzzyWorks() {
        var soT = SuggestOptions.builder().fuzzy(true).build();
        var soF = SuggestOptions.builder().fuzzy(false).build();
        var soE = SuggestOptions.builder().build();

        assertEquals("&fuzzy=true", soT.toQueryParameters());
        assertEquals("", soF.toQueryParameters());
        assertEquals("", soE.toQueryParameters());
    }

    @Test
    void allCompositionWorks() {
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
        assertEquals(longQueryParams, so.toQueryParameters());
    }
}
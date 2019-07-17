import com.microsoft.azure.search.samples.options.SearchOptions;
import org.junit.jupiter.api.Test;

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
        assertEquals("&$select=listingId&2C+street&2C+status&2C+daysOnMarket&2C+description", so.toQueryParameters());
    }
}

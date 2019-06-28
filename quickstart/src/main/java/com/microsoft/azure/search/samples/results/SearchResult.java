package com.microsoft.azure.search.samples.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoValue
@JsonIgnoreProperties(value = { "@odata.context" })
public abstract class SearchResult {
    public abstract List<SearchHit> hits();

    public abstract long count();

    @JsonCreator
    public static SearchResult create(@JsonProperty("value") List<SearchHit> hits,@JsonProperty("@odata.count") long count) {
        return new com.microsoft.azure.search.samples.results.AutoValue_SearchResult(hits, count);
    }

    @AutoValue
    public abstract static class SearchHit {
        public abstract Map<String, Object> document();

        @JsonCreator
        public static SearchHit create(Map<String, Object> jsonMap) {
            Map<String, Object> document = jsonMap;
            return new com.microsoft.azure.search.samples.results.AutoValue_SearchResult_SearchHit(document);
        }
    }
}

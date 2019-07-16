package com.microsoft.azure.search.samples.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoValue
@JsonIgnoreProperties(value = { "@odata.context" })
public abstract class SearchResult {
    public abstract List<SearchHit> hits();

    public abstract long count();

    @Nullable
    public abstract String nextLink();

    @Nullable
    public abstract Double coverage();

    @Nullable
    public abstract Map<String, FacetValue[]> facets();

    @JsonCreator
    public static SearchResult create(@JsonProperty("value") List<SearchHit> hits,
            @JsonProperty("@odata.count") long count, @JsonProperty("@odata.nextLink") String nextLink,
            @JsonProperty("@search.coverage") Double coverage,
            @JsonProperty("@search.facets") Map<String, FacetValue[]> facets) {
        return new com.microsoft.azure.search.samples.results.AutoValue_SearchResult(hits, count, nextLink, coverage,
                                                                                     facets);
    }

    @AutoValue
    public abstract static class SearchHit {
        public abstract Map<String, Object> document();

        @Nullable
        public abstract Map<String, String[]> highlights();

        public abstract double score();

        @JsonCreator
        public static SearchHit create(Map<String, Object> jsonMap) {
            double score = (Double) jsonMap.get("@search.score");
            jsonMap.remove("@search.score");
            Map<String, String[]> highlights = null;
            if (jsonMap.containsKey("@search.highlights")) {
                highlights = (Map<String, String[]>) jsonMap.get("@search.highlights");
                ArrayList<String> keys = new ArrayList<String>();
                for (String k : highlights.keySet()) {
                    if (k.endsWith("@odata.type")) {
                        keys.add(k);
                    }
                }
                for (String k : keys) {
                    highlights.remove(k);
                }
                jsonMap.remove("@search.highlights");
            }
            Map<String, Object> document = jsonMap;
            return new com.microsoft.azure.search.samples.results.AutoValue_SearchResult_SearchHit(document, highlights,
                                                                                                   score);
        }
    }

    @AutoValue
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class FacetValue {
        @Nullable
        public abstract Object value();

        @Nullable
        public abstract Object from();

        @Nullable
        public abstract Object to();

        public abstract Integer count();

        @JsonCreator
        public static FacetValue create(@JsonProperty("value") Object value, @JsonProperty("from") Object from,
                @JsonProperty("to") Object to, @JsonProperty("count") Integer count) {
            return new com.microsoft.azure.search.samples.results.AutoValue_SearchResult_FacetValue(value, from, to,
                                                                                                    count);
        }
    }
}

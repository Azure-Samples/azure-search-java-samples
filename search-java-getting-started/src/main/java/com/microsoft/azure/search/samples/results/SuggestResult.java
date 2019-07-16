package com.microsoft.azure.search.samples.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.List;

@AutoValue
@JsonIgnoreProperties(value = { "@odata.context" })
public abstract class SuggestResult {

    public abstract List<SuggestHit> hits();

    @Nullable

    public abstract Double coverage();

    @JsonCreator
    public static SuggestResult create(@JsonProperty("value") List<SuggestHit> hits,
            @JsonProperty("@search.coverage") Double coverage) {
        return new com.microsoft.azure.search.samples.results.AutoValue_SuggestResult(hits, coverage);
    }
}

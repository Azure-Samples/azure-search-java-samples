package com.microsoft.azure.search.samples.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.List;

@AutoValue
@JsonIgnoreProperties(value = { "@odata.context" })
public abstract class IndexBatchResult {
    public abstract List<IndexBatchOperationResult> value();

    @Nullable
    public abstract Integer status();

    @JsonCreator
    public static IndexBatchResult create(@JsonProperty("value") List<IndexBatchOperationResult> value,
            @JsonProperty("status") Integer status) {
        return new com.microsoft.azure.search.samples.results.AutoValue_IndexBatchResult(value, status);
    }
}

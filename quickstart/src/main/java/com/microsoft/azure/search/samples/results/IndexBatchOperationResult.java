package com.microsoft.azure.search.samples.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

@AutoValue
public abstract class IndexBatchOperationResult {
    public abstract String key();

    public abstract boolean status();

    @Nullable
    public abstract String errorMessage();

    public abstract int statusCode();

    @JsonCreator
    public static IndexBatchOperationResult create(@JsonProperty("key") String key,
            @JsonProperty("status") boolean status, @JsonProperty("errorMessage") String errorMessage,
            @JsonProperty("statusCode") int statusCode) {
        return new com.microsoft.azure.search.samples.results.AutoValue_IndexBatchOperationResult(key, status,
                                                                                                  errorMessage,
                                                                                                  statusCode);
    }
}

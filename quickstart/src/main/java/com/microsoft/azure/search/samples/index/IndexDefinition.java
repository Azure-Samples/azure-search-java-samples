package com.microsoft.azure.search.samples.index;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class IndexDefinition {
    public abstract String name();

    public abstract List<IndexField> fields();

    @JsonCreator
    public static IndexDefinition create(@JsonProperty("name") String name,
            @JsonProperty("fields") List<IndexField> fields
) {
        return new com.microsoft.azure.search.samples.index.AutoValue_IndexDefinition(name, fields);
    }
}

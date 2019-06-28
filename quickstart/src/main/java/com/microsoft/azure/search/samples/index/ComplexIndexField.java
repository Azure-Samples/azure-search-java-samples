package com.microsoft.azure.search.samples.index;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class ComplexIndexField implements IndexField {
    public abstract String name();

    public abstract List<IndexField> fields();

    public abstract String type();

    @JsonCreator
    public static ComplexIndexField create(@JsonProperty("name") String name,
            @JsonProperty("fields") List<IndexField> fields, boolean isCollection) {
        String type = isCollection ? "Collection(Edm.ComplexType)" : "Edm.ComplexType";
        return new com.microsoft.azure.search.samples.index.AutoValue_ComplexIndexField(name, fields, type);
    }
}

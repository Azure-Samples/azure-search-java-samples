package com.microsoft.azure.search.samples.index;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class Suggester {
    public abstract String name();

    public abstract String searchMode();

    public abstract List<String> sourceFields();

    public static Suggester create(String name, String searchMode, List<String> sourceFields) {
        return new com.microsoft.azure.search.samples.index.AutoValue_Suggester(name, searchMode, sourceFields);
    }
}

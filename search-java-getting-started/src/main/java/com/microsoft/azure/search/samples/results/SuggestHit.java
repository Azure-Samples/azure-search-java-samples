package com.microsoft.azure.search.samples.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
public abstract class SuggestHit {
    public abstract String text();

    public abstract Map<String, Object> document();

    @JsonCreator
    public static SuggestHit create(Map<String, Object> jsonMap) {
        String text = (String) jsonMap.get("@search.text");
        jsonMap.remove("@search.text");
        return new com.microsoft.azure.search.samples.results.AutoValue_SuggestHit(text, jsonMap);
    }
}

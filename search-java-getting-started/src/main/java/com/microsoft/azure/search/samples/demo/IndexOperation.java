package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/*
 * Only "uploadOperation" and "deleteOperation" are modeled here, there is
 * also "merge" and "mergeOrUpload" that can capture different scenarios.
 */
public class IndexOperation {
    private Map<String, Object> payload;

    private IndexOperation(Map<String, Object> map) {
        this.payload = map;
    }

    @JsonValue
    private Map<String, Object> toJson() {
        return this.payload;
    }

    static IndexOperation uploadOperation(Object object) {
        Map<String, Object> map = new ObjectMapper().convertValue(object, Map.class);
        map.put("@search.action", "upload");
        return new IndexOperation(map);
    }

    static IndexOperation deleteOperation(String keyName, String keyValue) {
        Map<String, Object> map = new HashMap<>();
        map.put(keyName, keyValue);
        map.put("@search.action", "delete");

        return new IndexOperation(map);
    }
}

package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/*
 * Only "uploadOperation" and "deleteOperation" are modeled here, there is
 * also "merge" and "mergeOrUpload" that can capture different scenarios.
 */
public class IndexOperation {
    private static  ObjectMapper MAPPER  = new ObjectMapper();

    static Map<String, Object> deleteOperation(String keyName, String keyValue) {
        Map<String, Object> map = new HashMap<>();
        map.put(keyName, keyValue);
        return map;
    }
}

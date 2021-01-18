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

    static String uploadOperation(Object object) throws JsonProcessingException {
        Map<String, Object> map = new ObjectMapper().convertValue(object, Map.class);
        map.put("@search.action", "upload");
        return MAPPER.writeValueAsString(map);
    }

    static String deleteOperation(String keyName, String keyValue) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put(keyName, keyValue);
        map.put("@search.action", "delete");
        return MAPPER.writeValueAsString(map);
    }
}

package com.example.crmservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    /*
     * Required for converting DTOs to JSON for MockMVC requests.
     * TODO Migrate to RestAssured to avoid needing the below method
     */
    public static String toJson(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            System.out.println(jsonContent);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

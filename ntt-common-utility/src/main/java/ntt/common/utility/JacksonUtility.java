/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-20
 * Description : Create Json Utility
 * * Dùng Jackson khi:
 * ✅ Need high performance (API production, microservices)
 * ✅ Handling big JSON(streaming)
 * ✅ Need advanced features (polymorphism, custom serializers)
 * ✅ Project Spring Boot (Jackson là default)
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */

package ntt.common.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class JacksonUtility {

    public static ObjectMapper initializeObjectMapper(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        mapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.registerModule(new JavaTimeModule()); //Serialization and deserialization of java LocalDateTime
        return mapper;
    }

    private static void getAllJsonNodeFieldNames(JsonNode jsonNode, Set<String> keys) {
        if (jsonNode.isObject()) {
            Iterator<String> fieldNames = jsonNode.fieldNames();
            fieldNames.forEachRemaining(fieldName -> {
                keys.add(fieldName);
                getAllJsonNodeFieldNames(jsonNode.get(fieldName), keys);
            });
        } else if (jsonNode.isArray()) {
            ArrayNode arrayField = (ArrayNode) jsonNode;
            arrayField.forEach(node -> {
                getAllJsonNodeFieldNames(node, keys);
            });
        }
    }

    public static Set<String> getAllJsonNodeFieldNames(String json) {
        try {
            if(StringUtils.isEmpty(json)) return null;
            final ObjectMapper mapper = initializeObjectMapper();
            Set<String> keys = new HashSet<>();
            JsonNode jsonNode = mapper.readTree(json);
            getAllJsonNodeFieldNames(jsonNode, keys);
            return keys;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static Set<String> getAllJsonNodeFieldNames(JsonNode jsonNode) {
        try {
            if(jsonNode == null) return null;
            final ObjectMapper mapper = initializeObjectMapper();
            Set<String> keys = new HashSet<>();
            getAllJsonNodeFieldNames(jsonNode, keys);
            return keys;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            if(StringUtils.isEmpty(json)) return null;
            final ObjectMapper mapper = initializeObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static ObjectNode toObjectNode(String json) {
        try {
            if(StringUtils.isEmpty(json)) return null;
            final ObjectMapper mapper = initializeObjectMapper();
            ObjectNode objectNode = (ObjectNode) mapper.readTree(json);
            return objectNode;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static String toJson(ObjectNode objectNode){
        try{
            if(objectNode == null) return null;
            return objectNode.toString();
        } catch (Exception ex){
            return null;
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            if(StringUtils.isEmpty(json) || clazz == null) return null;
            final ObjectMapper mapper = initializeObjectMapper();
            String normalJson = json.replaceAll("\\\\n", " ");
            T model = (T) mapper.readValue(normalJson, clazz);
            return model;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static <T> T toObject(ObjectNode node, Class<T> clazz) {
        try {
            if(node == null || clazz == null) return null;
            final ObjectMapper mapper = initializeObjectMapper();
            T model = (T) mapper.treeToValue(node, clazz);
            return model;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        try {
            if(StringUtils.isEmpty(json) || clazz == null) return null;
            final ObjectMapper mapper = initializeObjectMapper();
            JavaType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, clazz);
            List<T> collection = (List<T>) mapper.readValue(json, listType);
            return collection;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static ArrayNode toArrayNode(String json){
        try{
            if(StringUtils.isEmpty(json)) return null;
            final ObjectMapper mapper = initializeObjectMapper();
            ArrayNode array = (ArrayNode) mapper.readTree(json);
            return array;
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }


    public static boolean getPropertyValueAsBoolean(JsonNode node, String fieldName){
        if(!node.has(fieldName)){
            return false;
        }
        boolean result = node.get(fieldName).asBoolean(false);
        return result;
    }


    public static String getPropertyValueAsString(JsonNode node, String fieldName){
        if(!node.has(fieldName)){
            return null;
        }
        String result = node.get(fieldName).asText();
        return result.trim();
    }

}

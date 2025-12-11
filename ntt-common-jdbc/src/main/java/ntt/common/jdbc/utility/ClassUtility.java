/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-12
 * Description : Create ClassUtility to handle returned Sql record from Jdbc
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */

package ntt.common.jdbc.utility;

import ntt.common.jdbc.model.FieldInfo;
import ntt.common.utility.JacksonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ClassUtility {
    public static HashMap<String, FieldInfo> getAllFields(HashMap<String, FieldInfo> fields, Class<?> clazz){
        if(fields == null) {
            fields = new HashMap<>();
        }
        if(clazz == null){
            return fields;
        }
        Field[] fieldList = clazz.getDeclaredFields();
        for(Field field: fieldList){
            String fieldName = field.getName();
            String fieldType = field.getType().getName();
            String lowercaseKey = fieldName.replaceAll("_","").toLowerCase();
            if(!fields.containsKey(lowercaseKey)){
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(fieldName);
                fieldInfo.setFieldType(fieldType);
                fields.put(lowercaseKey, fieldInfo);
            }
        }
        //Recursive super class
        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null){
            getAllFields(fields, superClass);
        }
        return fields;
    }

    public static <T> T castMapToModel(Map<String, Object> map, Class<T> clazz, HashMap<String, FieldInfo> hashClassField){
        List<String> keySet = new ArrayList<>(map.keySet());
        ObjectMapper mapper = JacksonUtility.initializeObjectMapper();
        ObjectNode jsonModel = mapper.createObjectNode();
        for(String key: keySet){
            String lowercaseKey = key.replaceAll("_","").toLowerCase();
            if(hashClassField.containsKey(lowercaseKey)){
                FieldInfo fieldInfo = hashClassField.get(lowercaseKey);
                String modelFieldName = fieldInfo.getFieldName();
                String modelFieldType = fieldInfo.getFieldType();
                Object dbValue = map.get(key);
                String strDbValue = dbValue == null? null: String.valueOf(dbValue);
                if(StringUtils.isEmpty(strDbValue)){
                    continue;
                }

                if(modelFieldType.toLowerCase().contains("bool")){
                    Boolean modelValue = "true".equals(strDbValue) || "1".equals(strDbValue)? true : false;
                    jsonModel.put(modelFieldName, modelValue);
                } else {
                    String modelValue = strDbValue;
                    jsonModel.put(modelFieldName, modelValue);
                }
            }
        }
        T result = JacksonUtility.toObject(jsonModel, clazz);
        return result;
    }

    public static Map<String, String> buildJsonKeys(Map<String, Object> map){
        List<String> keySet = new ArrayList<>(map.keySet());
        Map<String, String> result = new HashMap<>();
        for(String key: keySet){
            String jsonKey =  Pattern.compile("_([a-z])")
                    .matcher(key)
                    .replaceAll(m -> m.group(1).toUpperCase());
            result.put(key, jsonKey);
        }
        return result;
    }

    public static JsonNode castMapToJsonNode(Map<String, Object> map, Map<String, String> jsonKeys){
        List<String> keySet = new ArrayList<>(map.keySet());
        final ObjectMapper mapper = JacksonUtility.initializeObjectMapper();
        JsonNode jsonNode = mapper.createObjectNode();
        for(String key: keySet){
            String jsonKey = jsonKeys.get(key);
            Object dbValue = map.get(key);
            if(dbValue == null) continue;
            if(dbValue.equals(true) || dbValue.equals(false)){
                ((ObjectNode) jsonNode).put(jsonKey, (boolean) dbValue);
            } else {
                ((ObjectNode) jsonNode).put(jsonKey, dbValue.toString());
            }
        }
        return jsonNode;
    }
    public static  <T> boolean isPrimitiveWrapper(Class<T> clazz){
        if(clazz == null) return true;
        String classType = clazz.getTypeName();
        if(classType.equals(String.class.getTypeName())) return true;
        else if(classType.equals(Integer.class.getTypeName())) return true;
        else if(classType.equals(Boolean.class.getTypeName())) return true;
        else if(classType.equals(Float.class.getTypeName())) return true;
        else if(classType.equals(Long.class.getTypeName())) return true;
        else if(classType.equals(Short.class.getTypeName())) return true;
        else if(classType.equals(Double.class.getTypeName())) return true;
        else if(classType.equals(Object.class.getTypeName())) return true;
        else if(classType.equals(Byte.class.getTypeName())) return true;
        else if(classType.equals(Character.class.getTypeName())) return true;
        else if(classType.equals(Void.class.getTypeName())) return true;
        return false;
    }
}

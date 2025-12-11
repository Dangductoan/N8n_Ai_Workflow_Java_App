package ntt.common.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataConverter {
    public static <T> List<T> parseJsonToList(String json, Class<T> elementType) throws IOException{
        Gson gson = new Gson();
        Type listType = TypeToken.getParameterized(ArrayList.class, elementType).getType();
        List<T> collection = gson.fromJson(json, listType);
        return collection;
    }

    public static <T> List<T> parseJsonToList(String json, String path, Class<T> clazz){
        Gson gson = new Gson();
        String[] paths = path.split("\\.");
        JsonObject o = gson.fromJson(json, JsonObject.class);
        for(int i = 0; i< paths.length -1; i++){
            o = o.getAsJsonObject(paths[i]);
        }
        JsonArray jsonArray = o.getAsJsonArray(paths[paths.length-1]);
        Class<T[]> clazzArray = (Class<T[]>) ((T[]) Array.newInstance(clazz, 0)).getClass();
        T[] objectArray = gson.fromJson(jsonArray, clazzArray);
        return Arrays.asList(objectArray);
    }

    public static <T> T parseJsonToObject(String json, Class<T> modelType) throws IOException{
        Gson gson = new Gson();
        T model = gson.fromJson(json, modelType);
        return model;
    }

    public static <T> T copy(Object fromModel, Class<T> toModel){
        Gson gson = new Gson();
        JsonObject jModel = gson.toJsonTree(fromModel).getAsJsonObject();
        String json = gson.toJson(jModel);
        T model = gson.fromJson(json, toModel);
        return model;
    }
    public static String toJsonString(Object obj){
        if(obj == null) return null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String strJson = gson.toJson(obj);
        return strJson;
    }

}

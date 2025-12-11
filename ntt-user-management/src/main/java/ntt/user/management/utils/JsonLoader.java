package ntt.user.management.utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class JsonLoader {

    public Map<String, Object> loadJsonFromResource(String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Load from classpath (resources folder)
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }

        // Convert to Map<String, Object>
        return mapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
    }

}
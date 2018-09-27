package kz.stanislav.voting.web.json;

import java.util.Collections;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {
    private final ObjectMapper objectMapper;

    @Autowired
    public JsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> String writeIgnoreProps(Collection<T> collection, String... ignoreProps) {
        List<Map<String, Object>> list = collection.stream()
                .map(e -> getAsMapWithIgnore(e, ignoreProps))
                .collect(Collectors.toList());

        return writeValue(list);
    }

    public <T> String writeIgnoreProps(T obj, String... ignoreProps) {
        Map<String, Object> map = getAsMapWithIgnore(obj, ignoreProps);

        return writeValue(map);
    }

    public <T> String writeValue(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid write to JSON:\n'" + obj + "'", e);
        }
    }

    public <T> String writeAdditionProps(T obj, String addName, Object addValue) {
        return writeAdditionProps(obj, Collections.singletonMap(addName, addValue));
    }

    public <T> String writeAdditionProps(T obj, Map<String, Object> addProps) {
        Map<String, Object> map = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
        map.putAll(addProps);
        return writeValue(map);
    }

    public <T> T readValue(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON:\n'" + json + "'", e);
        }
    }

    private <T> Map<String, Object> getAsMapWithIgnore(T obj, String[] ignoreProps) {
        Map<String, Object> map = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
        for (String prop : ignoreProps) {
            map.remove(prop);
        }

        return map;
    }
}
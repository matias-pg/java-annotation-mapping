package dev.matiaspg.annotationsmapping.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JsonReader {
    private final ObjectMapper mapper;
    private final ResourceLoader resourceLoader;

    @SneakyThrows(IOException.class)
    public JsonNode readJson(String path) {
        Resource resource = resourceLoader.getResource("classpath:json/" + path + ".json");
        return mapper.readTree(resource.getInputStream());
    }
}

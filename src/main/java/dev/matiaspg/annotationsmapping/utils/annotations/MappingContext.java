package dev.matiaspg.annotationsmapping.utils.annotations;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.BiFunction;

public record MappingContext(BiFunction<JsonNode, Class<?>, ?> recursive) {
    @SuppressWarnings("unchecked")
    public <T> T recurse(JsonNode node, Class<T> targetClass) {
        // It's safe to cast to T since JsonMapper#map always returns T
        return (T) recursive.apply(node, targetClass);
    }
}

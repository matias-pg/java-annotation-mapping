package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.util.function.BiFunction;

@Builder
public record MappingContext<T>(
    ObjectMapper mapper,
    BiFunction<JsonNode, Class<?>, ?> recursive
) {
}

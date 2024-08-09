package dev.matiaspg.annotationsmapping.utils.annotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.util.function.BiFunction;

@Builder
public record MappingContext(
    ObjectMapper mapper,
    BiFunction<JsonNode, Class<?>, ?> recursive
) {
}

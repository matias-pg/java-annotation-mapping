package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface Mapper<T> {
    Mono<T> map(JsonNode input);
}

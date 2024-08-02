package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Component
public class ClassMappers {
    private final Map<Class<?>, Collection<BiConsumer<JsonNode, Object>>> mappers =
        new ConcurrentHashMap<>();

    public Collection<BiConsumer<JsonNode, Object>> getOrCreateMapper(
        Class<?> targetClass, Supplier<Collection<BiConsumer<JsonNode, Object>>> supplier) {
        return mappers.computeIfAbsent(targetClass, target -> supplier.get());
    }
}

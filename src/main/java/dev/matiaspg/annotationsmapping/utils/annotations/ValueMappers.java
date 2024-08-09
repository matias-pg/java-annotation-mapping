package dev.matiaspg.annotationsmapping.utils.annotations;

import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class ValueMappers {
    private final Map<Class<?>, Collection<ValueMapper>> mappers =
        new ConcurrentHashMap<>();

    public Collection<ValueMapper> getOrCreateMapper(
        Class<?> targetClass, Supplier<Collection<ValueMapper>> supplier) {
        return mappers.computeIfAbsent(targetClass, target -> supplier.get());
    }
}

package dev.matiaspg.annotationsmapping.utils.annotations;

import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class ValueGetters {
    private final Map<Class<?>, ValueGetter> getters =
        new ConcurrentHashMap<>();

    public ValueGetter getOrCache(
        Class<?> targetClass, Supplier<ValueGetter> supplier) {
        return getters.computeIfAbsent(targetClass, target -> supplier.get());
    }
}

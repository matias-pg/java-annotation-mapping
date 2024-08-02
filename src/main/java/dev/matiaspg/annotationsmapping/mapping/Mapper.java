package dev.matiaspg.annotationsmapping.mapping;

public interface Mapper<T> {
    Class<T> getTargetClass();
}

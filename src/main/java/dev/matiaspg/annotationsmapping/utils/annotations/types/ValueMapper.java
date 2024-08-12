package dev.matiaspg.annotationsmapping.utils.annotations.types;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.BiConsumer;

/**
 * A function that maps the value of a {@link JsonNode} to an object (instance
 * of a class).
 * <p>
 * To do so, it may update object fields, or call some of its methods (e.g.
 * setters).
 */
public interface ValueMapper extends BiConsumer<JsonNode, Object> {
}

package dev.matiaspg.annotationsmapping.utils.annotations.types;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.BiConsumer;

/**
 * A function that maps values from a {@link JsonNode} to an object instance.
 * <p>
 * To do so, it may update object fields, or call some of its methods (e.g.
 * setters).
 */
public interface ValueMapper extends BiConsumer<JsonNode, Object> {
}

package dev.matiaspg.annotationsmapping.utils.annotations.types;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Predicate;

/**
 * A function that filters items from a {@link JsonNode}.
 */
public interface ItemFilter extends Predicate<JsonNode> {
    /**
     * Allows ALL items from a {@link JsonNode}.
     */
    interface AllowAllItems extends ItemFilter {
    }
}

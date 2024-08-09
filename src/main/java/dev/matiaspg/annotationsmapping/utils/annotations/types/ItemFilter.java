package dev.matiaspg.annotationsmapping.utils.annotations.types;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

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

    /**
     * Includes items that have a field with true.
     */
    @RequiredArgsConstructor
    class IncludeIfTrue implements ItemFilter {
        private final String fieldPath;

        @Override
        public boolean test(JsonNode jsonNode) {
            return jsonNode.at(fieldPath).asBoolean();
        }
    }

    /**
     * Excludes items that have a field with true.
     */
    @RequiredArgsConstructor
    class ExcludeIfTrue implements ItemFilter {
        private final String fieldPath;

        @Override
        public boolean test(JsonNode jsonNode) {
            return !jsonNode.at(fieldPath).asBoolean();
        }
    }
}

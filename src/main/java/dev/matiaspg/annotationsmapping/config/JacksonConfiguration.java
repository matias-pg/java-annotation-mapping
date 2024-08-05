package dev.matiaspg.annotationsmapping.config;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer configureNodeFactory() {
        return builder -> builder.postConfigurer(mapper -> mapper
            // To make NullNodes return null rather than "null" when calling asText()
            .setNodeFactory(CustomJsonNodeFactory.instance));
    }

    public static class CustomJsonNodeFactory extends JsonNodeFactory {
        public static final JsonNodeFactory instance = new CustomJsonNodeFactory();

        /**
         * Factory method for getting an instance of JSON null node (which
         * represents literal null value)
         */
        @Override
        public NullNode nullNode() {
            return CustomNullNode.instance;
        }

        public static class CustomNullNode extends NullNode {
            public static final NullNode instance = new CustomNullNode();

            /**
             * Method that will return {@code null} value rather than
             * {@code "null"} string
             */
            @Override
            public String asText() {
                return null;
            }
        }
    }
}

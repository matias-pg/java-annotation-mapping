package dev.matiaspg.annotationsmapping.annotations;

import com.fasterxml.jackson.databind.node.TextNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface ConcatMapFrom {
    String NO_DEFAULT_VALUE = "";

    /**
     * Delimiter used to join the values that were found.
     */
    String delimiter() default "";

    /**
     * Paths from where to map the field.
     */
    String[] paths();

    /**
     * Default value to use when all paths were null or missing.
     * <p>
     * Its value will be passed to {@link TextNode#valueOf(String)}.
     */
    String defaultValue() default NO_DEFAULT_VALUE;

    /**
     * Whether you want to use an empty string as a default value.
     *
     * @implNote Since an empty string is used to indicate that you don't want
     * a default value, by setting this to true an empty string will be used as
     * a default value.
     */
    boolean defaultEmptyString() default false;
}

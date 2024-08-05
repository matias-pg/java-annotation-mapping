package dev.matiaspg.annotationsmapping.mapping.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MapEachFrom {
    /**
     * Path from where to map the field items.
     */
    String value();

    /**
     * Class to which each item will be mapped.
     * <p>
     * Ideally this should be retrieved from the field type, but since Java
     * removes generic types after compilation *when no super class is being
     * used*, we have to use some "hacks" that can fail, in which case you
     * would have to pass the type here
     *
     * @deprecated LOL. May be deleted in the future since doesn't seem
     * necessary for now
     */
    // TODO: DELETE
    Class<?> itemType() default Object.class;

    /**
     * Whether to do the mapping in parallel.
     * <p>
     * Disabled by default since it would only make sense when mapping hundreds
     * or thousands of items, otherwise it would be a waste of resources when
     * most of the time only a few items are mapped.
     * <p>
     * I suggest doing a benchmark first before enabling this.
     */
    boolean parallel() default false;

    // TODO: DELETE
    /*// TODO: Allow passing passing a class that implements Predicate<T>
    //  where T is the mapped class, or JsonNode to filter the raw nodes
    Predicate<JsonNode> filterBy();

    // TODO: Allow passing passing a class that implements Comparator<T>
    //  where T is the mapped class, or JsonNode to sort the raw nodes
    Comparator<JsonNode> sortBy();

    // TODO: Use an enum for this
    String direction();*/
}

package dev.matiaspg.annotationsmapping.annotations;

import dev.matiaspg.annotationsmapping.utils.annotations.types.ItemFilter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MapEachFrom {
    /**
     * Path from where to map the field items.
     */
    String value();

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

    /**
     * Class used to filter the items to be mapped.
     */
    Class<? extends ItemFilter> itemFilter() default ItemFilter.AllowAllItems.class;

    /**
     * Arguments to be passed when initializing the filter class.
     */
    String[] itemFilterArgs() default {};
}

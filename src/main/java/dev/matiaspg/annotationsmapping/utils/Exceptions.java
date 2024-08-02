package dev.matiaspg.annotationsmapping.utils;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @see <a href="https://github.com/diffplug/durian/blob/master/src/com/diffplug/common/base/Errors.java">Errors.java</a>
 * @see <a href="https://github.com/diffplug/durian/blob/master/src/com/diffplug/common/base/Throwing.java">Throwing.java</a>
 */
public class Exceptions {
    public static <T, R> Function<T, R> wrap(ThrowingFunction<T, R, Throwable> fn) {
        return var1 -> {
            try {
                return fn.apply(var1);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T, U> BiConsumer<T, U> wrap(ThrowingBiConsumer<T, U, Throwable> fn) {
        return (var1, var2) -> {
            try {
                fn.accept(var1, var2);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Throwable> {
        R apply(T var1) throws E;
    }

    @FunctionalInterface
    public interface ThrowingBiConsumer<T, U, E extends Throwable> {
        void accept(T var1, U var2) throws E;
    }
}

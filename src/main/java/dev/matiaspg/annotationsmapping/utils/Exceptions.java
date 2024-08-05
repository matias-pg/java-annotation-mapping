package dev.matiaspg.annotationsmapping.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see <a href="https://github.com/diffplug/durian/blob/master/src/com/diffplug/common/base/Errors.java">Errors.java</a>
 * @see <a href="https://github.com/diffplug/durian/blob/master/src/com/diffplug/common/base/Throwing.java">Throwing.java</a>
 */
@Slf4j
// TODO: Evaluate if it´s really necessary to have this
// TODO: DELETE
public class Exceptions {
    public static <T> Optional<T> inOptional(ThrowingSupplier<T, Throwable> fn) {
        try {
            return Optional.ofNullable(fn.get());
        } catch (Throwable e) {
            log.error("Error in Optional", e);
            return Optional.empty();
        }
    }

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

    public static <T> Supplier<T> wrap(ThrowingSupplier<T, Throwable> fn) {
        return () -> {
            try {
                return fn.get();
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

    @FunctionalInterface
    public interface ThrowingSupplier<T, E extends Throwable> {
        T get() throws E;
    }
}

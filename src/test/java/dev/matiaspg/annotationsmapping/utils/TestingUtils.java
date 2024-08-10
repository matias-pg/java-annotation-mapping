package dev.matiaspg.annotationsmapping.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.NONE)
@Slf4j
public class TestingUtils {
    public static <T> void testMapping(Mono<T> resultMono, Supplier<JsonNode> expected) {
        long startTime = System.nanoTime();
        StepVerifier.create(resultMono).assertNext(result -> {
            long duration = System.nanoTime() - startTime;
            log.info("Mapping took {} nanoseconds ({} milliseconds)", duration, duration / 1_000_000);

            JsonAssertions.assertThatJson(result).isEqualTo(expected.get());
        }).verifyComplete();
    }
}

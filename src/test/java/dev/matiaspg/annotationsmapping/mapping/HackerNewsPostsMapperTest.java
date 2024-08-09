package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.JsonReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

// TODO: Load necessary classes only
@Slf4j
@SpringBootTest
public class HackerNewsPostsMapperTest {
    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private HackerNewsPostsMapper mapper;

    @Test
    void mapJson() {
        JsonNode stories = jsonReader.readJson("input/hackernews/selfhost");

        StepVerifier.create(mapper.map(stories)).assertNext(result -> {
            log.info("Mapping result: {}", result);

            JsonNode expected = jsonReader.readJson("expected_output/hackernews");

            assertThatJson(result).isEqualTo(expected);
        }).verifyComplete();
    }
}

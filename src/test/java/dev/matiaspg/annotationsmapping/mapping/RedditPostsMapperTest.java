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
public class RedditPostsMapperTest {
    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private RedditPostsMapper mapper;

    @Test
    void mapJson() {
        JsonNode posts = jsonReader.readJson("input/reddit/selfhost");

        StepVerifier.create(mapper.map(posts)).assertNext(result -> {
            log.info("Mapping result: {}", result);

            JsonNode expected = jsonReader.readJson("expected_output/reddit");

            assertThatJson(result).isEqualTo(expected);
        }).verifyComplete();
    }
}

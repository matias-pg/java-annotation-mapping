package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.JsonReader;
import dev.matiaspg.annotationsmapping.utils.TestingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// TODO: Load necessary classes only
@SpringBootTest
public class RedditPostsMapperTest {
    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private RedditPostsMapper mapper;

    @Test
    void map() {
        JsonNode posts = jsonReader.readJson("reddit/input/selfhost");

        TestingUtils.testMapping(mapper.map(posts),
            () -> jsonReader.readJson("reddit/expected_output/selfhost"));
    }
}

package dev.matiaspg.annotationsmapping.services.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.reddit.RedditPosts;
import dev.matiaspg.annotationsmapping.mapping.JsonMapping;
import dev.matiaspg.annotationsmapping.utils.JsonReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

// TODO: Load necessary classes only
@SpringBootTest
public class RedditPostsMapperTest {
    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private JsonMapping jsonMapping;

    @Autowired
    private RedditPostsMapper mapper;

    @Test
    void mapJson() {
        JsonNode posts = jsonReader.readJson("input/reddit/selfhost");
        RedditPosts result = jsonMapping.mapJson(posts, mapper.getTargetClass());

        JsonNode expected = jsonReader.readJson("expected_output/reddit");

        assertThatJson(result).isEqualTo(expected);
    }
}

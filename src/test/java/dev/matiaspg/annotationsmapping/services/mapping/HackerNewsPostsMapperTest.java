package dev.matiaspg.annotationsmapping.services.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPosts;
import dev.matiaspg.annotationsmapping.mapping.JsonMapping;
import dev.matiaspg.annotationsmapping.services.consumers.HackerNewsService;
import dev.matiaspg.annotationsmapping.utils.JsonReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

// TODO: Load necessary classes only
@SpringBootTest
public class HackerNewsPostsMapperTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JsonMapping jsonMapping;

    @Autowired
    private HackerNewsPostsMapper mapper;

    @Autowired
    private HackerNewsService hackerNewsService;

    @Autowired
    private JsonReader jsonReader;

    @Test
    void mapJson() {
        JsonNode stories = hackerNewsService.getStories();
        HackerNewsPosts result = jsonMapping.mapJson(stories, mapper.getTargetClass());

        JsonNode expected = jsonReader.readJson("expected_output/hackernews");

        assertThatJson(result).isEqualTo(expected);
    }
}

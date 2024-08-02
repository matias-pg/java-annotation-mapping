package dev.matiaspg.annotationsmapping.services.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.matiaspg.annotationsmapping.dto.reddit.RedditPosts;
import dev.matiaspg.annotationsmapping.mapping.JsonMapping;
import dev.matiaspg.annotationsmapping.services.consumers.RedditService;
import dev.matiaspg.annotationsmapping.utils.JsonReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// TODO: Load necessary classes only
@SpringBootTest
public class RedditPostsMapperTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JsonMapping jsonMapping;

    @Autowired
    private RedditPostsMapper mapper;

    @Autowired
    private RedditService service;

    @Autowired
    private JsonReader jsonReader;

    @Test
    void mapJson() {
        JsonNode posts = service.getPosts();
        RedditPosts result = jsonMapping.mapJson(posts, mapper.getTargetClass());

        JsonNode expected = jsonReader.readJson("expected_output/reddit");

        Assertions.assertEquals(expected, objectMapper.valueToTree(result));
    }
}

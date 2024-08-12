package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.JsonReader;
import dev.matiaspg.annotationsmapping.utils.TestingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// TODO: Load necessary classes only
@SpringBootTest
public class CarMakesMapperTest {
    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private CarMakesMapper mapper;

    @Autowired
    private CarMakesClassicMapper classicMapper;

    @Test
    void map() {
        JsonNode posts = jsonReader.readJson("nhtsa/input/carmakes");

        TestingUtils.testMapping(mapper.map(posts),
            () -> jsonReader.readJson("nhtsa/expected_output/carmakes"));
    }

    @Test
    void mapClassic() {
        JsonNode posts = jsonReader.readJson("nhtsa/input/carmakes");

        TestingUtils.testMapping(classicMapper.map(posts),
            () -> jsonReader.readJson("nhtsa/expected_output/carmakes"));
    }
}

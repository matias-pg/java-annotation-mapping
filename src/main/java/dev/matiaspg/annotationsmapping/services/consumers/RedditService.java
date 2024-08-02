package dev.matiaspg.annotationsmapping.services.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.JsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedditService {
    private final JsonReader jsonReader;

    public JsonNode getPosts() {
        return jsonReader.readJson("reddit/selfhost");
    }
}

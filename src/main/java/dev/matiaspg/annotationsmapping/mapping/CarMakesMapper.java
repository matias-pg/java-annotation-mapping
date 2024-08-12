package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.nhtsa.CarMakes;
import dev.matiaspg.annotationsmapping.utils.annotations.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CarMakesMapper implements Mapper<CarMakes> {
    private final JsonMapper jsonMapper;

    @Override
    public Mono<CarMakes> map(JsonNode input) {
        return Mono.fromSupplier(() -> jsonMapper.map(input, CarMakes.class));
    }
}

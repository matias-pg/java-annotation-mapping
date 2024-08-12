package dev.matiaspg.annotationsmapping.dto.nhtsa;

import dev.matiaspg.annotationsmapping.annotations.MapEachFrom;
import dev.matiaspg.annotationsmapping.annotations.MapFrom;
import lombok.Data;

import java.util.List;

@Data
public class CarMakes {
    @MapEachFrom("/Results")
    private List<Make> makes;

    @Data
    public static class Make {
        @MapFrom("/Make_ID")
        private Integer id;

        @MapFrom("/Make_Name")
        private String name;
    }
}

package dev.matiaspg.annotationsmapping.dto.reddit;

import dev.matiaspg.annotationsmapping.annotations.MapEachFrom;
import dev.matiaspg.annotationsmapping.annotations.MapFrom;
import lombok.Data;

@Data
public class Image {
    @MapFrom("/source")
    private Source source;

    // Works with array types too
    @MapEachFrom("/resolutions")
    private Source[] resolutions;

    // Nested classes are also supported
    @Data
    public static class Source {
        @MapFrom("/url")
        private String url;

        @MapFrom("/width")
        private Integer width;

        @MapFrom("/height")
        private Integer height;
    }
}

# Annotations mapping

Map deep and complex JSONs to POJOs using _configurable_ annotations.

Need to change some annotation, but you don't have time to create a release and deploy it? Don't worry, just update the
properties file and you're good to go!

> Work in progress! This is just a proof of concept where the two most basic annotations (`@MapFrom` and `@MapEachFrom`)
> are already working, but there are more annotations that have to be implemented.
>
> Besides that, the code should be improved. I created this in a couple of hours and my main goal was to get something
> working, so there are things that could definitely be done better.
>
> Also, check the `TODO` in the `JsonMapping` class, where I talk about another implementation that could allow for a
> simpler code.

## Annotations

### `@MapFrom` and `@MapEachFrom`

These are the most basic annotations and the ones you will use the most:

- `@MapFrom`: maps a single field
- `@MapEachFrom`: maps multiple objects from an array

#### Examples

Assuming you have DTOs like this:

```java
package dev.matiaspg.annotationsmapping.dto;

@Data
public class HackerNewsPosts {
    @MapEachFrom("/hits")
    private List<HackerNewsPost> posts;
}

@Data
public class HackerNewsPost {
    @MapFrom("/story_id")
    private String id;

    @MapFrom("/title")
    private String title;

    @MapFrom("/points")
    private Integer points;

    @MapFrom("/num_comments")
    private Integer numberOfComments;

    @MapFrom("/url")
    private String url;
}
```

And a JSON like this:

```json
{
  "hits": [
    {
      "num_comments": 403,
      "points": 610,
      "story_id": 25300396,
      "title": "Self-host your fonts for better performance",
      "url": "https://wicki.io/posts/2020-11-goodbye-google-fonts/"
    },
    {
      "num_comments": 335,
      "points": 538,
      "story_id": 21235957,
      "title": "Ask HN: What do you self-host?"
    }
  ]
}
```

The resulting mapping will be this:

```json
[
  {
    "id": "25300396",
    "title": "Self-host your fonts for better performance",
    "points": 610,
    "numberOfComments": 403,
    "url": "https://wicki.io/posts/2020-11-goodbye-google-fonts/"
  },
  {
    "id": "21235957",
    "title": "Ask HN: What do you self-host?",
    "points": 538,
    "numberOfComments": 335,
    "url": null
  }
]
```

#### Options

TODO

### `@AfterMapping`

TODO

### `@ConcatMapFrom`

TODO

### `@MapManually`

TODO

## Configuring annotations

This app supports "configuring" annotations from:

- Class fields
- Class methods
- Parameters of class methods
- Class themselves (not used for now, but it may be useful in the future)

To do that, you have to define an array of "replacements" in your properties file with a JSON syntax.

### From a class field

Required fields:

- `targetClass`: full "path" of the class containing the annotation
    - Example: `dev.matiaspg.annotationsmapping.dto.HackerNewsPost`
- `targetField`: name of the field containing the annotation
- `annotationClass`: the full "path" of the annotation class whose values you want to replace
    - Example: `dev.matiaspg.annotationsmapping.annotations.MapFrom`
- `replacements`: key-value map with the name of the annotation method you want to replace, and the new value

### From a class method

Required fields:

- `targetClass`: full "path" of the class containing the annotation
    - Example: `dev.matiaspg.annotationsmapping.dto.HackerNewsPost`
- `targetMethod`: name of the method containing the annotation
- `annotationClass`: the full "path" of the annotation class whose values you want to replace
    - Example: `dev.matiaspg.annotationsmapping.annotations.MapFrom`
- `replacements`: key-value map with the name of the annotation method you want to replace, and the new value

### From a parameter of a class method

- `targetClass`: full "path" of the class containing the annotation
    - Example: `dev.matiaspg.annotationsmapping.dto.HackerNewsPost`
- `targetMethod`: name of the method
- `targetMethodParam`: name of the parameter containing the annotation
- `annotationClass`: the full "path" of the annotation class whose values you want to replace
    - Example: `dev.matiaspg.annotationsmapping.annotations.MapFrom`
- `replacements`: key-value map with the name of the annotation method you want to replace, and the new value

### From a class itself

> There is no mapping annotation that you can apply on classes as of now, so this is _useless_ for now. Still, once a
> use case is found, it will be there.

- `targetClass`: full "path" of the class containing the annotation
    - Example: `dev.matiaspg.annotationsmapping.dto.HackerNewsPost`
- `annotationClass`: the full "path" of the annotation class whose values you want to replace
    - Example: `dev.matiaspg.annotationsmapping.annotations.MapFrom`
- `replacements`: key-value map with the name of the annotation method you want to replace, and the new value

### Full example

Assuming you have the `HackerNewsPost` (notice singular) DTO from above and this updated `HackerNewsPosts` DTO:

```java

@Data
public class HackerNewsPosts {
    private List<HackerNewsPost> posts;
    private String query;
    private Boolean nsfw;

    @MapEachFrom("/hits")
    private void setPosts(List<HackerNewsPost> posts) {
        this.posts = posts;
    }

    private void setMultipleValues(
        @MapFrom("/meta/query") String query,
        @MapFrom("/meta/nsfw") Boolean nsfw
    ) {
        this.query = query;
        this.nsfw = nsfw;
    }
}
```

You can replace annotation values by putting the following in your `application.properties` file:

```properties
dev.matiaspg.mapping.annotationReplacements=[\
  {\
    "targetClass": "dev.matiaspg.annotationsmapping.dto.HackerNewsPost",\
    "targetField": "id",\
    "annotationClass": "dev.matiaspg.annotationsmapping.annotations.MapFrom",\
    "replacements": { "value": ["/v2/story_id"] }\
  },\
  {\
    "targetClass": "dev.matiaspg.annotationsmapping.dto.HackerNewsPosts",\
    "targetMethod": "setPosts",\
    "annotationClass": "dev.matiaspg.annotationsmapping.annotations.MapEachFrom",\
    "replacements": { "value": "/v2/hits" }\
  },\
  {\
    "targetClass": "dev.matiaspg.annotationsmapping.dto.HackerNewsPosts",\
    "targetMethod": "setMultipleValues",\
    "targetMethodParam": "query",\
    "annotationClass": "dev.matiaspg.annotationsmapping.annotations.MapFrom",\
    "replacements": { "value": ["/v2/meta/query", "/v3/meta/query"] }\
  }\
]

```

> I know it looks kind of ugly, but it's because `properties` files require you to escape new lines. In YAML it would
> probably look much better.

With those replacements, the following things changed:

- The `id` field from the `HackerNewsPost` (singular) DTO now works as if it was annotated
  with `@MapFrom("/v2/story_id")`
- The `setPosts` method from the `HackerNewsPosts` (plural) DTO now works as if it was annotated
  with `@MapEachFrom("/v2/hits")`
- The `query` parameter from the `setMultipleValues` method from the `HackerNewsPosts` (plural) DTO now works as if it
  was annotated with `@MapFrom({"/v2/meta/query", "/v3/meta/query"})`
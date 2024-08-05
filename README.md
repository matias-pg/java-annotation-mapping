# Annotations mapping

Map deep and complex JSONs to POJOs using annotations.

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

### `@MapAnyOf`

TODO

### `@MapManually`

TODO
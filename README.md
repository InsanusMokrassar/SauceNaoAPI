# SauceNaoAPI

It is wrapper for [SauceNAO](https://saucenao.com/) API. For now, library is
in preview state. It can be fully used, but some of info can be unavailable from
wrapper classes.

## Requester

For the requests we are using `SauceNaoAPI` object. Unfortunately, for now it
supports only url strings as source of request. For example:

```kotlin
val key = // here must be your Sauce NAO API key
val requestUrl = // here must be your link to some image

val api = SauceNaoAPI(key)
api.use {
    println(
        it.request(requestUrl)
    )
}
```

Most of others requests use the same etymology and meaning as in the
`SauceNAO` API docs.

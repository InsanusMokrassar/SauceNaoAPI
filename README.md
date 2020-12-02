# SauceNaoAPI

 [ ![Download](https://api.bintray.com/packages/insanusmokrassar/SauceNaoAPI/saucenaoapi/images/download.svg) ](https://bintray.com/insanusmokrassar/SauceNaoAPI/saucenaoapi/_latestVersion)

It is wrapper for [SauceNAO](https://saucenao.com/) API. For now, library is
in preview state. It can be fully used, but some of info can be unavailable from
wrapper classes, but now you can access them via `SauceNaoAnswer#row` field.

## Including

### Gradle

```groovy
implementation "com.insanusmokrassar:SauceNaoAPI:$saucenaoapi_version"
```

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

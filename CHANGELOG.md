# SauceNaoAPI Changelog

## 0.5.0

* Versions updates

### 0.5.1

## 0.4.0

* Update libraries versions
    * Kotlin `1.3.31` -> `1.3.50`
    * Coroutines `1.2.1` -> `1.3.2`
    * Serialization `0.11.0` -> `0.13.0`
    * Joda Time `2.10.1` -> `2.10.4`
    * Ktor `1.1.4` -> `1.2.5`
* Now `SauceNaoAPI` is `Closeable`
* Now `SauceNaoAPI` working with synchronous queue
* `SauceNaoAPI` now will wait for some time when one of limits will be achieved

### 0.4.4

* Uploading of file
* Updates of versions
* Now `SauceNaoAPI` do not require api key
* `SauceNaoAPI` instances now can return `limitsState` object, which will contains `LimitsState` with currently known
state of limits

### 0.4.3

Hotfix for serializer of `SauceNaoAnswer`

### 0.4.2

Hotfix for autostop for some time when there is no remaining quotas for requests

### 0.4.1 Managers experiments and row format in answer

* Add `TimeManager` - it will manage work with requests times
* Add `RequestQuotaMagager` - it will manage quota for requests and call suspend
if they will be over
* `SauceNaoAPI` now working (almost) asynchronously
* Now `SauceNaoAnswer` have field `row` which contains `JsonObject` with
all original answer fields

## 0.3.0

* Now `results` field of `SauceNaoAnswer` is optional and is empty list by default
* Adapted structure almost completed and now can be used with raw results


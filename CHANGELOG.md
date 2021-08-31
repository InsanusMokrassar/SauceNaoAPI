# SauceNaoAPI Changelog

## 0.9.0

* Versions updates:
  * `Kotlin`: `1.5.10` -> `1.5.30`
  * `Klock`: `2.1.2` -> `2.4.0`
  * `Ktor`: `1.5.4` -> `1.6.3`
  * `Serialization`: `1.2.1` -> `1.2.2`
  * `Coroutines`: `1.5.0` -> `1.5.1`

## 0.8.2

* Versions updates:
  * `Kotlin`: `1.4.32` -> `1.5.10`
  * `Klock`: `2.0.7` -> `2.1.2`
  * `Ktor`: `1.5.3` -> `1.5.4`
  * `Serialization`: `1.1.0` -> `1.2.1`
  * `Coroutines`: `1.4.3` -> `1.5.0`

## 0.8.1

* Versions updates:
  * `Kotlin`: `1.4.31` -> `1.4.32`
  * `Klock`: `2.0.6` -> `2.0.7`
  * `Ktor`: `1.5.2` -> `1.5.3`

## 0.8.0

* Versions updates:
  * `Kotlin`: `1.4.21` -> `1.4.31`
  * `Klock`: `2.0.4` -> `2.0.6`
  * `Ktor`: `1.5.1` -> `1.5.2`
  * `Kotlin Serialisation`: `1.0.1` -> `1.1.0`
  * `Kotlin Coroutines`: `1.4.2` -> `1.4.3`

## 0.7.2

* Versions updates:
  * `Klock`: `2.0.2` -> `2.0.4`
  * `Ktor`: `1.5.0` -> `1.5.1`

## 0.7.1

* Versions updates:
  * `Kotlin`: `1.4.20` -> `1.4.21`
  * `Klock`: `2.0.0` -> `2.0.2`
  * `Ktor`: `1.4.3` -> `1.5.0`

## 0.7.0

**BREAKING CHANGES: PACKAGE HAS BEEN CHANGED FROM `com.insanusmokrassar` to `dev.inmo`**

Migration:

* Packages in the whole project were changed `com.insanusmokrassar.SauceNaoAPI` -> `dev.inmo.saucenaoapi`
* Change implementation in your gradle files: `implementation "com.insanusmokrassar:SauceNaoAPI:*"` ->
`implementation "dev.inmo:saucenaoapi:*"`

## 0.6.2

* Versions updates:
  * `Kotlin`: `1.4.10` -> `1.4.20`
  * `Kotlin Serialisation`: `1.0.0-RC2` -> `1.0.1`
  * `Kotlin Coroutines`: `1.3.9` -> `1.4.2`
  * `Klock`: `1.12.1` -> `2.0.0`
  * `Ktor`: `1.4.1` -> `1.4.3`

## 0.6.1

* Versions updates:
  * `Kotlin`: `1.4.0` -> `1.4.10`
  * `Kotlin Serialisation`: `1.0.0-RC` -> `1.0.0-RC2`
  * `Klock`: `1.12.0` -> `1.12.1`
  * `Ktor`: `1.4.0` -> `1.4.1`

## 0.6.0

**MAIN PACKAGE WAS CHANGED: `com.github.insanusmokrassar` -> `com.insanusmokrassar`**

* All known fields were added to `ResultData`
* Versions updates:
    * `Kotlin`: `1.3.72` -> `1.4.0`
    * `Coroutines`: `1.3.8` -> `1.3.9`
    * `Serialization`: `0.20.0` -> `1.0.0-RC`
    * `Klock`: `1.11.14` -> `1.12.0`
    * `Ktor`: `1.3.2` -> `1.4.0`

## 0.5.0

* Versions updates

## 0.4.4

* Uploading of file
* Updates of versions
* Now `SauceNaoAPI` do not require api key
* `SauceNaoAPI` instances now can return `limitsState` object, which will contains `LimitsState` with currently known
state of limits

## 0.4.3

Hotfix for serializer of `SauceNaoAnswer`

## 0.4.2

Hotfix for autostop for some time when there is no remaining quotas for requests

## 0.4.1 Managers experiments and row format in answer

* Add `TimeManager` - it will manage work with requests times
* Add `RequestQuotaMagager` - it will manage quota for requests and call suspend
if they will be over
* `SauceNaoAPI` now working (almost) asynchronously
* Now `SauceNaoAnswer` have field `row` which contains `JsonObject` with
all original answer fields

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

## 0.3.0

* Now `results` field of `SauceNaoAnswer` is optional and is empty list by default
* Adapted structure almost completed and now can be used with raw results


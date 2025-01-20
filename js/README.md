# Android JavaScript Library

A Kotlin-based Android library that provides seamless JavaScript execution and integration capabilities using Mozilla's Rhino engine.

## Features

- Execute JavaScript code from strings or files
- Built-in functions for network requests (fetch, xhr)
- Base64 encoding/decoding support
- Console logging
- File importing capability
- JSON conversion utilities
- Coroutine support for asynchronous operations

## Installation

Add the following dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.paulcoding:js:1.0.0")  // Replace with actual version
}
```

## Initialization

Initialize the library in your Application class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        JS.initialize(applicationContext)
    }
}
```

## Usage

### Basic JavaScript Execution

```kotlin
// Create JS instance
val js = JS()

// Execute JavaScript string
suspend fun executeJs() {
    js.evaluateString<String>("'Hello, World!'")
        .onSuccess { result ->
            println(result) // Prints: Hello, World!
        }
        .onFailure { error ->
            error.printStackTrace()
        }
}

// Call JavaScript function
suspend fun callJsFunction() {
    js.callFunction<Int>("add", arrayOf(2, 3))
        .onSuccess { result ->
            println(result) // Prints: 5
        }
}
```

### Loading JavaScript from File

```kotlin
// Load from file in app's files directory
val js = JS("scripts", "main.js")

// Or with full path
val js = JS(File("/path/to/script.js"))
```

### Built-in Functions

The library provides several built-in JavaScript functions:

#### fetch()
```javascript
const html = fetch('https://example.com')
```

#### xhr()
```javascript
const data = xhr('https://api.example.com/data')
```

#### import()
```javascript
import('utils.js')
```

#### console.log()
```javascript
console.log('Debug message')
```

#### atob()
```javascript
const decoded = atob('SGVsbG8gV29ybGQ=')
```

### Custom Properties

You can pass custom properties to the JavaScript environment:

```kotlin
val properties = mapOf(
    "apiKey" to "your-api-key",
    "baseUrl" to "https://api.example.com"
)
val js = JS(properties = properties)
```

## Dependencies

- [Rhino](https://github.com/mozilla/rhino): JavaScript engine
- [Ktor](https://ktor.io/): HTTP client
- [Gson](https://github.com/google/gson): JSON parsing
- [JSoup](https://jsoup.org/): HTML parsing

## Proguard

```
-keepattributes Signature
-dontwarn org.mozilla.javascript.**
-keep class org.mozilla.javascript.** { *; }
-keep class org.jsoup.** { *; }
-dontwarn org.jspecify.annotations.NullMarked
```

## Requirements

- Minimum SDK: 24
- Kotlin: 1.8+
- Java: 11

## License

[License](LICIENSE)

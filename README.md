# kotlinx-charset

![Build status](https://github.com/lppedd/kotlinx-charset/workflows/Build/badge.svg)
![MIT license](https://img.shields.io/github/license/lppedd/kotlinx-charset)
![Kotlin 2.1.20](https://img.shields.io/badge/kotlin-2.1.20-blue.svg?logo=kotlin)

Minimal charset support for Kotlin Multiplatform.

### Changelog

See [CHANGELOG.md](./CHANGELOG.md).

### Supported Kotlin platforms

All Kotlin platforms are supported, except for Android.

## core

The `core` module provides the foundational components for implementing
and registering new charsets.

```kotlin
// Create an empty charset registry
private val registrar = XCharsetRegistrar()

// Register a new charset
registrar.registerCharset(YourCharset())

// Retrieve and use a charset
val charset = registrar.getCharset("yourCharsetName")
val decoder = charset.newDecoder()
val encoder = charset.newEncoder()
```

## ebcdic

The `ebcdic` module adds support for:

```text
IBM037
IBM930
IBM1047
```

You can register supported EBCDIC charsets to your `XCharsetRegistrar`
via the `provideCharsets` function.

```kotlin
import com.github.lppedd.kotlinx.charset.ebcdic.provideCharsets as provideEbcdicCharsets

// Your shared charset registry
private val registrar = XCharsetRegistrar()

provideEbcdicCharsets(registrar)
```

## exported

The `exported` module allows JS (and soon WebAssembly) consumers to decode bytes
and encode strings, using top-level functions exported via ECMAScript modules.

> [!TIP]
> Avoid using this module when consuming `kotlinx-charset` from a Kotlin project

You can depend on the [@lppedd/kotlinx-charset][1] npm package.  
For example, consuming the library from TypeScript would look like:

```ts
import { decode, encode } from "@lppedd/kotlinx-charset";

function example(bytes: Uint8Array): Uint8Array {
  const str = decode("cp037", bytes);
  return encode("cp037", str);
}
```

Both the `decode` and `encode` functions will throw an `Error`
if the specified charset does not exist or if an error occurs
during data processing.

[1]: https://www.npmjs.com/package/@lppedd/kotlinx-charset

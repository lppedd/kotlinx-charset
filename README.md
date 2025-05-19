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
IBM037  IBM939   IBM1390
IBM273  IBM1047  IBM1399
IBM297  IBM1141
IBM930  IBM1147
```

You can register supported EBCDIC charsets to your `XCharsetRegistrar`
via the `provideCharsets` function.

```kotlin
import com.lppedd.kotlinx.charset.ebcdic.provideCharsets as provideEbcdicCharsets

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
import { decode, encode, getCharset } from "@lppedd/kotlinx-charset";

function funsExample(bytes: Uint8Array): Uint8Array {
  const str = decode("ibm037", bytes);
  return encode("ibm037", str);
}

// Alternatively, you can interact with a charset instance directly.
// This allows setting or removing (by passing null) the replacement character.
function instanceExample(bytes: Uint8Array): Uint8Array {
  const charset = getCharset("ibm037");

  const decoder = charset.newDecoder();
  const str = decoder.decode(bytes);

  const encoder = charset.newEncoder();
  return encoder.encode(str);
}
```

Both the `decode` and `encode` functions will throw an `Error`
if the specified charset does not exist or if an error occurs
during data processing.

[1]: https://www.npmjs.com/package/@lppedd/kotlinx-charset

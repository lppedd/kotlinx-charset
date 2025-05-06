# kotlinx-charset

Charset support for Kotlin Multiplatform.

## core

The `core` module provides the building blocks to implement new charsets,
and to store them in a registry.

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
> If you are consuming kotlinx-charset from a Kotlin project, avoid using this module

You can depend on the [@lppedd/kotlinx-charset][1] npm package.  
For example, consuming the library from TypeScript would look like:

```ts
import { decode, encode } from "@lppedd/kotlinx-charset";

function example(toDecode: Uint8Array): void {
  const str = decode(toDecode);
  const bytes = encode(str);
}
```

[1]: https://www.npmjs.com/package/@lppedd/kotlinx-charset

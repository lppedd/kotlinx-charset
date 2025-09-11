# kotlinx-charset changelog

## 0.1.4

- Updated Kotlin to version 2.2.20.
- Cleaned up build scripts.

## 0.1.3

- Added a function to retrieve a charset without throwing an exception in case it is missing.

## 0.1.2

- Added support for retrieving all registered charset instances.
- Corrected EBCDIC double byte charset encoding behavior for unmappable code points.  
  JDK delegation on the JVM has been removed to avoid its incorrect behavior.
- Applied minor code refactorings.

## 0.1.1

- Published modules to Maven Central.
- Applied a small refactoring to `EbcdicDbcsEncoder`.

## 0.1.0

- Added support for IBM1390 and IBM1399 character sets.
- Implemented a parser for `ucm` files.
- Introduced a basic CLI for converting `ucm` files to `map`, `nr`, and `c2b` formats (internal use only).
- Added a comprehensive test suite covering most supported charsets.
- Applied various refactorings to improve maintainability.

## 0.0.4

- Added support for IBM273, IBM297, IBM939, IBM1141, and IBM1147 character sets.
- Added checks for replacement sequences on non-JVM platforms.  
  Setting a custom replacement sequence now throws an exception
  if it is incompatible with the decoder or encoder.
- Applied minor refactorings.

## 0.0.3

- Added a `getCharset(name)` function to the `exported` module for retrieving `XCharset` instances.
- Made the built-in charset registry in the `exported` module eagerly initialized.
- Applied various refactorings.

## 0.0.2

- Fixed SBCS and DBCS processing.

## 0.0.1

Initial release.

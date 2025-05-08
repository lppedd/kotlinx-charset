# kotlinx-charset changelog

## 0.0.4

- Added support for IBM273, IBM297, IBM939, IBM1141, and IBM1147
- Added checks for replacement sequences on non-JVM platforms.  
  Setting a custom replacement sequence now throws an exception
  if it is incompatible with the decoder or encoder.
- Applied minor refactorings

## 0.0.3

- Added a `getCharset(name)` function to the `exported` module for retrieving `XCharset` instances.
- Made the built-in charset registry in the `exported` module eagerly initialized.
- Applied various refactorings

## 0.0.2

- Fixed SBCS and DBCS processing.

## 0.0.1

Initial release.

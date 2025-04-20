package com.github.lppedd.kotlinx.charset

internal fun Int.firstByte(): Int =
  (this shr 8) and 0xFF

internal fun Int.secondByte(): Int =
  this and 0xFF

internal fun Int.toHex(length: Int = 4): String =
  String.format("0x%0${length}X", this)

internal fun Int.toUnicodeChar(): String =
  String.format("\\u%04X", this)

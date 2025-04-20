@file:OptIn(ExperimentalStdlibApi::class)

package com.github.lppedd.kotlinx.charset

private val printFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    prefix = "0x"
  }
}

internal fun Int.toHex(): String =
  this.toHexString(printFormat)

internal fun Char.toHex(): String =
  this.code.toHexString(printFormat)

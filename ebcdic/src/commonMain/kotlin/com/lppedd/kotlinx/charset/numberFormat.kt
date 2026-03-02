// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

private val printFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 2
    prefix = "0x"
  }
}

internal fun Int.toHex(): String =
  this.toHexString(printFormat)

internal fun Char.toHex(): String =
  this.code.toHexString(printFormat)

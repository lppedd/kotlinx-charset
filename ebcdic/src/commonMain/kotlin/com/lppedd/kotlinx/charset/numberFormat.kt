// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

@OptIn(ExperimentalStdlibApi::class)
private val printFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 2
    prefix = "0x"
  }
}

@OptIn(ExperimentalStdlibApi::class)
internal fun Int.toHex(): String =
  this.toHexString(printFormat)

@OptIn(ExperimentalStdlibApi::class)
internal fun Char.toHex(): String =
  this.code.toHexString(printFormat)

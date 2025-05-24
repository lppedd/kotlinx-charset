// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ebcdic

import com.lppedd.kotlinx.charset.decodeToHexString
import com.lppedd.kotlinx.charset.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals

// IBM1399 is very similar to IBM1390.
// They only differ in certain single byte mappings.
class IBM1399Test {
  @Test
  fun decodeSingleBytes() {
    val bytes = byteArrayOf(0x42.toByte(), 0x57.toByte(), 0x58.toByte(), 0x59.toByte())
    val hex = IBM1399.decodeToHexString(bytes)
    assertEquals("\uFF61\uFF6F\uFF70\uFF71".toHexString(), hex)
  }

  @Test
  fun decodeUnmapped() {
    val bytes = byteArrayOf(0x41.toByte(), 0xA6.toByte())
    val hex = IBM1399.decodeToHexString(bytes)
    assertEquals("\uFFFD\u0077".toHexString(), hex)
  }
}

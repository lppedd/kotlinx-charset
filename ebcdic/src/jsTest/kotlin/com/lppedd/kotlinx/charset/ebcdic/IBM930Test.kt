// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ebcdic

import com.lppedd.kotlinx.charset.Ebcdic.SO
import com.lppedd.kotlinx.charset.decodeToHexString
import com.lppedd.kotlinx.charset.encodeToHexString
import com.lppedd.kotlinx.charset.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("unused")
class IBM930Test : B2CTest(IBM930, "IBM930.b2c") {
  @Test
  fun encodeUnmappable() {
    var hex = IBM930.encodeToHexString("\uE843\u814A")
    assertEquals("0E,74,5A,0F,6F", hex)

    hex = IBM930.encodeToHexString("\u0001\u814A\uE843", byteArrayOf(0xFE.toByte(), 0xFE.toByte()))
    assertEquals("01,0E,FE,FE,74,5A,0F", hex)
  }

  @Test
  fun bugJdk8368845() {
    var hex = IBM930.encodeToHexString("\uFF0D")
    assertEquals("0E,42,60,0F", hex)

    hex = IBM930.decodeToHexString(byteArrayOf(SO, 0x42.toByte(), 0x60.toByte()))
    assertEquals("\uFF0D".toHexString(), hex)
  }
}

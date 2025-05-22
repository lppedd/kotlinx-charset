package com.lppedd.kotlinx.charset.ebcdic

import com.lppedd.kotlinx.charset.encodeToHexString
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("unused")
class IBM930Test : B2CTest(IBM930, "IBM930.b2c") {
  @Test
  fun encodeUnmappable() {
    var hex = IBM930.encodeToHexString("\uE843\u814A")
    assertEquals("0E,74,5A,6F,0F", hex)

    hex = IBM930.encodeToHexString("\u814A\uE843", byteArrayOf(0xFE.toByte(), 0xFE.toByte()))
    assertEquals("FE,FE,0E,74,5A,0F", hex)
  }
}

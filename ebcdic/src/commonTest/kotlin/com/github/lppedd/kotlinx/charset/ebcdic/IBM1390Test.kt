package com.github.lppedd.kotlinx.charset.ebcdic

import com.github.lppedd.kotlinx.charset.Ebcdic.SO
import com.github.lppedd.kotlinx.charset.decodeToHexString
import com.github.lppedd.kotlinx.charset.encodeToHexString
import com.github.lppedd.kotlinx.charset.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IBM1390Test {
  @Test
  fun decodeSingleBytes() {
    val bytes = byteArrayOf(
      0x2B.toByte(),
      0xF4.toByte(),
      0x41.toByte(),
      0x15.toByte(),
      0x25.toByte(),
    )

    val hex = IBM1390.decodeToHexString(bytes)
    assertEquals("\u008B\u0034\uFF61\u000A\u000A".toHexString(), hex)
  }

  @Test
  fun decodeDoubleBytes() {
    val bytes = byteArrayOf(SO.toByte(), 0xBA.toByte(), 0x60.toByte(), 0xCC.toByte(), 0x47.toByte())
    val hex = IBM1390.decodeToHexString(bytes)
    assertEquals("\u5427\u86B1".toHexString(), hex)
  }

  @Test
  fun decodeComposites() {
    val compositeByteSeq = arrayOf(
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xB5.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xB6.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xB7.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xB8.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xB9.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xBA.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xBB.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xBC.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xBD.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xBE.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xBF.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC0.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC1.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC2.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC3.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC4.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC5.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC6.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC7.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC8.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xC9.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xCA.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xCB.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xCC.toByte()),
      byteArrayOf(SO.toByte(), 0xEC.toByte(), 0xCD.toByte()),
    )

    val compositeChars = arrayOf(
      "\u304B\u309A",
      "\u304D\u309A",
      "\u304F\u309A",
      "\u3051\u309A",
      "\u3053\u309A",
      "\u30AB\u309A",
      "\u30AD\u309A",
      "\u30AF\u309A",
      "\u30B1\u309A",
      "\u30B3\u309A",
      "\u30BB\u309A",
      "\u30C4\u309A",
      "\u30C8\u309A",
      "\u31F7\u309A",
      "\u00E6\u0300",
      "\u0254\u0300",
      "\u0254\u0301",
      "\u028C\u0300",
      "\u028C\u0301",
      "\u0259\u0300",
      "\u0259\u0301",
      "\u025A\u0300",
      "\u025A\u0301",
      "\u02E9\u02E5",
      "\u02E5\u02E9",
    )

    // Test all composite byte sequences, one by one
    for (i in compositeByteSeq.indices) {
      val hex = IBM1390.decodeToHexString(compositeByteSeq[i])
      assertEquals(compositeChars[i].toHexString(), hex)
    }

    // Test a couple of random composite byte sequences
    val bytes = byteArrayOf(
      SO.toByte(),    // Start of double byte mode
      0xEC.toByte(),
      0xC4.toByte(),
      0xEC.toByte(),
      0xC9.toByte(),
    )

    val hex = IBM1390.decodeToHexString(bytes)
    assertEquals("\u0254\u0300\u0259\u0301".toHexString(), hex)
  }

  @Test
  fun decodeMixedBytes() {
    val bytes = byteArrayOf(
      0x2B.toByte(),  // Single bytes
      0xF4.toByte(),
      SO.toByte(),    // Start of double bytes mode
      0x4E.toByte(),
      0xA9.toByte(),
      0xB4.toByte(),
      0xB0.toByte(),
      0xEC.toByte(),  // Composite
      0xB5.toByte(),
    )

    val hex = IBM1390.decodeToHexString(bytes)
    assertEquals("\u008B\u0034\u68DF\uD84D\uDC4B\u304B\u309A".toHexString(), hex)
  }

  @Test
  fun decodeUnmapped() {
    val bytes = byteArrayOf(
      0x0C.toByte(),
      SO.toByte(),    // Start of double bytes mode
      0xFA.toByte(),  // 0xFAFD is unmapped
      0xFD.toByte(),
      0xCC.toByte(),
      0x48.toByte(),
      0x42.toByte(),  // First byte of a double byte, but unmapped as there is no second byte
    )

    val hex = IBM1390.decodeToHexString(bytes)

    // Assert the unmapped bytes are decoded by replacement chars
    assertEquals("\u000C\uFFFD\u86B3\uFFFD".toHexString(), hex)

    // Assert it throws when no replacement char is defined
    assertFailsWith(CharacterCodingException::class) {
      IBM1390.decodeToHexString(bytes, null)
    }
  }

  @Test
  fun encodeSingleBytes() {
    val str = "\u003C\uFF84\u005C\u000A\u0085"
    val bytes = IBM1390.encodeToHexString(str)
    assertEquals("4C,95,B2,15,15", bytes)
  }

  @Test
  fun encodeDoubleBytes() {
    val str = "\uEF65\uEF66\u96CA"
    val hex = IBM1390.encodeToHexString(str)
    assertEquals("0E,7D,CE,7D,CF,D1,D3,0F", hex)
  }

  @Test
  fun encodeSurrogates() {
    //         |u27BBE     |u24103
    val str = "\uD85E\uDFBE\uD850\uDD03"
    val hex = IBM1390.encodeToHexString(str)
    assertEquals("0E,B6,EF,B5,6E,0F", hex)
  }

  @Test
  fun encodeComposites() {
    val composites = arrayOf(
      // Expected    Unicode code points
      "0E,EC,B5,0F", "\u304B\u309A",
      "0E,EC,B6,0F", "\u304D\u309A",
      "0E,EC,B7,0F", "\u304F\u309A",
      "0E,EC,B8,0F", "\u3051\u309A",
      "0E,EC,B9,0F", "\u3053\u309A",
      "0E,EC,BA,0F", "\u30AB\u309A",
      "0E,EC,BB,0F", "\u30AD\u309A",
      "0E,EC,BC,0F", "\u30AF\u309A",
      "0E,EC,BD,0F", "\u30B1\u309A",
      "0E,EC,BE,0F", "\u30B3\u309A",
      "0E,EC,BF,0F", "\u30BB\u309A",
      "0E,EC,C0,0F", "\u30C4\u309A",
      "0E,EC,C1,0F", "\u30C8\u309A",
      "0E,EC,C2,0F", "\u31F7\u309A",
      "0E,EC,C3,0F", "\u00E6\u0300",
      "0E,EC,C4,0F", "\u0254\u0300",
      "0E,EC,C5,0F", "\u0254\u0301",
      "0E,EC,C6,0F", "\u028C\u0300",
      "0E,EC,C7,0F", "\u028C\u0301",
      "0E,EC,C8,0F", "\u0259\u0300",
      "0E,EC,C9,0F", "\u0259\u0301",
      "0E,EC,CA,0F", "\u025A\u0300",
      "0E,EC,CB,0F", "\u025A\u0301",
      "0E,EC,CC,0F", "\u02E9\u02E5",
      "0E,EC,CD,0F", "\u02E5\u02E9",
    )

    // Test all composite characters, one by one
    for (i in composites.indices step 2) {
      val hex = IBM1390.encodeToHexString(composites[i + 1])
      assertEquals(composites[i], hex)
    }

    // Test a couple of random composite character
    //         |0xECB5     |0xECCD
    val str = "\u304B\u309A\u02E5\u02E9"
    val hex = IBM1390.encodeToHexString(str)
    assertEquals("0E,EC,B5,EC,CD,0F", hex)
  }

  @Test
  fun encodeMixedBytes() {
    //                                       |u27BBE     |0xECC1
    val str = "\u000C\uFF8B\uEF65\uEF66\u96CA\uD85E\uDFBE\u30C8\u309A"
    val hex = IBM1390.encodeToHexString(str)
    assertEquals("0C,9E,0E,7D,CE,7D,CF,D1,D3,B6,EF,EC,C1,0F", hex)
  }

  @Test
  fun encodeNonRoundtripC2B() {
    val str = "\u6805\u8346\uF86F"
    val hex = IBM1390.encodeToHexString(str)
    assertEquals("0E,51,F1,53,B3,44,6E,0F", hex)
  }

  @Test
  fun encodeNonRoundtripB2C() {
    // See x-IBM1390.nr
    val str = "\u20AC"
    val hex = IBM1390.encodeToHexString(str)
    assertEquals("E1", hex)
  }

  @Test
  fun encodeUnmapped() {
    val str = "\u6DC0\u0069\u10FF\u011D\u28BE\u0000\u008E\u31F7\u02A9"
    val hex = IBM1390.encodeToHexString(str)

    // u6DC0 = 0E,4A,88
    // u0069 = 0F,71
    // u10FF = 6F         UNMAPPED
    // u011D = 0E,D6,D5
    // u28BE = 6F         UNMAPPED
    // u0000 = 0F,00
    // u008E = 0A
    // u31F7 = 0E,EC,8C
    // u02A9 = 6F         UNMAPPED
    assertEquals("0E,4A,88,0F,71,6F,0E,D6,D5,6F,0F,00,0A,0E,EC,8C,6F,0F", hex)
  }
}

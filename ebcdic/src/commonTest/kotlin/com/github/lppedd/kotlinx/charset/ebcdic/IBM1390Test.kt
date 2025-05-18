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
    val bytes = byteArrayOf(0x2B.toByte(), 0xF4.toByte(), 0x41.toByte())
    val hex = IBM1390.decodeToHexString(bytes)
    assertEquals("\u008B\u0034\uFF61".toHexString(), hex)
  }

  @Test
  fun decodeDoubleBytes() {
    val bytes = byteArrayOf(SO.toByte(), 0xBA.toByte(), 0x60.toByte(), 0xCC.toByte(), 0x47.toByte())
    val hex = IBM1390.decodeToHexString(bytes)
    assertEquals("\u5427\u86B1".toHexString(), hex)
  }

  @Test
  fun decodeComposites() {
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
    val str = "\u003C\uFF84\u005C"
    val bytes = IBM1390.encodeToHexString(str)
    assertEquals("4C,95,B2", bytes)
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

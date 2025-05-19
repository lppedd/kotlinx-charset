package com.lppedd.kotlinx.charset

// Note: constants are inlined to trigger the K/JS eager-initialization optimization
//
// MIN_SUPPLEMENTARY_CODE_POINT   0x010000
// MIN_HIGH_SURROGATE             0xD800
// MIN_LOW_SURROGATE              0xDC00
// MAX_CODE_POINT                 0x10FFFF

/**
 * @author Edoardo Luppi
 */
internal object CodePoint {
  /**
   * Converts a Unicode code point to its UTF-16 representation.
   *
   * If the code point is inside the BMP (Basic Multilingual Plane),
   * the resulting array has a single element with the same value as
   * the code point.
   *
   * If the specified code point is a supplementary code point, the resulting
   * array has two elements representing the high and low surrogate pair.
   *
   * @param codePoint a Unicode code point
   * @throws IllegalArgumentException If the code point is not a valid Unicode code point
   */
  fun toChars(codePoint: Int): CharArray {
    if (isBmpCodePoint(codePoint)) {
      return charArrayOf(codePoint.toChar())
    }

    if (isValidCodePoint(codePoint)) {
      return toSurrogates(codePoint)
    }

    throw IllegalArgumentException("Not a valid Unicode code point: ${codePoint.toHex()}")
  }

  /**
   * Converts a surrogate pair to its supplementary code point value.
   *
   * This function does not validate the inputted surrogate pair.
   *
   * @param high The high surrogate code unit
   * @param low The low surrogate code unit
   */
  fun toCodePoint(high: Char, low: Char): Int =
    ((high.code shl 10) + low.code) + (0x010000 - (0xD800 shl 10) - 0xDC00)

  private fun toSurrogates(cp: Int): CharArray {
    val chars = CharArray(2)
    chars[0] = highSurrogate(cp)
    chars[1] = lowSurrogate(cp)
    return chars
  }

  private fun highSurrogate(cp: Int): Char =
    ((cp ushr 10) + (0xD800 - (0x010000 ushr 10))).toChar()

  private fun lowSurrogate(cp: Int): Char =
    ((cp and 0x3FF) + 0xDC00).toChar()

  private fun isBmpCodePoint(cp: Int): Boolean {
    val plane = getPlane(cp)
    return plane == 0
  }

  private fun isValidCodePoint(cp: Int): Boolean {
    val plane = getPlane(cp)
    return plane < (0x10FFFF + 1) ushr 16
  }

  private fun getPlane(cp: Int): Int =
    cp ushr 16
}

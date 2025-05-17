package com.github.lppedd.kotlinx.charset

// Note: an object which contains constants only is always inlined in K/JS,
//  so the CharsetMapping class acts purely as namespace

/**
 * @author Edoardo Luppi
 */
internal object CharsetMapping {
  const val UNMAPPABLE_DECODING: Char = '\uFFFD'
  const val UNMAPPABLE_ENCODING: Char = '\uFFFD'

  /**
   * The highest byte value that is considered a single-byte character.
   */
  const val MAX_SINGLE_BYTE: Int = 0xFF
}

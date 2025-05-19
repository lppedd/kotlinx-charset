package com.lppedd.kotlinx.charset

// Note: an object which contains constants only is always inlined in K/JS,
//  so the CharsetMapping class acts purely as namespace

/**
 * @author Edoardo Luppi
 */
internal object CharsetMapping {
  const val UNMAPPABLE_DECODING: Char = '\uFFFD'
  const val UNMAPPABLE_DECODING_INT: Int = 0xFFFD

  const val UNMAPPABLE_ENCODING: Char = '\uFFFD'
  const val UNMAPPABLE_ENCODING_INT: Int = 0xFFFD

  /**
   * The highest byte value that is considered a single-byte character.
   */
  const val MAX_SINGLE_BYTE: Int = 0xFF
}

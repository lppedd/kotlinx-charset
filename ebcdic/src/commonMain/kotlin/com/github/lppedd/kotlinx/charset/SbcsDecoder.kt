package com.github.lppedd.kotlinx.charset

import com.github.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_DECODING

/**
 * @author Edoardo Luppi
 */
internal class SbcsDecoder(private val b2c: CharArray) : XCharsetDecoder {
  private var replacement: Char? = '\uFFFD'

  override fun decode(bytes: ByteArray): String {
    reset()

    val size = bytes.size
    val sb = StringBuilder(size)

    for (i in 0..<size) {
      val b = bytes[i].toInt()
      val c = b2c[b]

      if (c == UNMAPPABLE_DECODING) {
        val repl = replOrThrow("Byte ${b.toHex()} is not mapped to a valid character")
        sb.append(repl)
      } else {
        sb.append(c)
      }
    }

    return sb.toString()
  }

  override fun withReplacement(newReplacement: String?): XCharsetDecoder {
    replacement = (if (!newReplacement.isNullOrEmpty()) newReplacement[0] else null)
    return this
  }

  override fun reset() {
    // There is no internal state to reset
  }

  private fun replOrThrow(message: String): Char {
    val repl = replacement

    if (repl != null) {
      return repl
    }

    throw MessageCharacterCodingException(message)
  }
}

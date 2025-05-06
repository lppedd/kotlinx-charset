package com.github.lppedd.kotlinx.charset

import com.github.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_ENCODING

/**
 * @author Edoardo Luppi
 */
internal class EbcdicDbcsEncoder(
  private val c2b: CharArray,
  private val c2bIndex: CharArray,
) : XCharsetEncoder {
  companion object {
    // The SBCS mode identifier
    const val SBCS: Int = 0

    // The DBCS mode identifier
    const val DBCS: Int = 1

    // Shift-Out (switch to DBCS mode)
    const val SO: Int = 0x0E

    // Shift-In (switch to SBCS mode)
    const val SI: Int = 0x0F

    // The highest byte value that is considered a single-byte character
    const val MAX_SINGLE_BYTE: Int = 0xFF
  }

  // The replacement character in case of malformed or unmappable bytes
  private var replacement: ByteArray? = byteArrayOf(0x6F)

  // The encoder starts in SBCS mode
  private var mode: Int = SBCS

  override fun encode(value: String): ByteArray {
    reset()

    val length = value.length
    val dst = ByteArray(length * 3)
    var sp = 0 // source position
    var dp = 0 // destination position

    while (sp < length) {
      val c = value[sp++]
      val bb = encodeChar(c)

      if (bb == UNMAPPABLE_ENCODING.code) {
        val repl = replOrThrow("Character ${c.toHex()} is not mapped to a valid byte sequence")

        if (c.isHighSurrogate() && sp < length && value[sp].isLowSurrogate()) {
          sp++
        }

        dst[dp++] = repl[0]

        if (repl.size > 1) {
          dst[dp++] = repl[1]
        }

        continue
      }

      if (bb > MAX_SINGLE_BYTE) {
        if (mode == SBCS) {
          mode = DBCS
          dst[dp++] = SO.toByte()
        }

        dst[dp++] = (bb shr 8).toByte()
        dst[dp++] = bb.toByte()
      } else {
        if (mode == DBCS) {
          mode = SBCS
          dst[dp++] = SI.toByte()
        }

        dst[dp++] = bb.toByte()
      }
    }

    if (mode == DBCS) {
      dst[dp++] = SI.toByte()
    }

    return dst.copyOf(dp)
  }

  override fun withReplacement(newReplacement: ByteArray?): XCharsetEncoder {
    replacement = newReplacement
    return this
  }

  override fun reset() {
    mode = SBCS
  }

  private fun replOrThrow(message: String): ByteArray {
    val repl = replacement

    if (repl != null) {
      return repl
    }

    throw MessageCharacterCodingException(message)
  }

  private fun encodeChar(ch: Char): Int {
    val high = ch.code shr 8
    val low = ch.code and 0xFF
    val index = c2bIndex[high].code
    val bytes = c2b[index + low]
    return bytes.code
  }
}

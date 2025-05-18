package com.github.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
internal class EbcdicDbcsEncoder(
  private val c2b: CharArray,
  private val c2bIndex: CharArray,
) : XCharsetEncoder {
  // The replacement character in case of malformed or unmappable bytes
  private var replacement: ByteArray? = byteArrayOf(0x6F)

  // The encoder starts in SBCS mode
  private var mode: Int = Ebcdic.SBCS

  override fun encode(value: String): ByteArray {
    reset()

    val length = value.length
    val dst = ByteArray(length * 4)
    var sp = 0 // source position
    var dp = 0 // destination position

    while (sp < length) {
      val c = value[sp++]
      val bb = encodeChar(c)

      if (bb == CharsetMapping.UNMAPPABLE_ENCODING.code) {
        val repl = replOrThrow("Character ${c.toHex()} is not mapped to a valid byte sequence")

        // In UTF-16, code points beyond uFFFF are encoded as surrogate pairs.
        // EBCDIC does not support surrogate pairs and considers them unmappable,
        // so we have to skip the second char (low surrogate).
        // The surrogate pair is replaced by 1 or 2 replacement bytes.
        if (c.isHighSurrogate() && sp < length && value[sp].isLowSurrogate()) {
          sp++
        }

        dst[dp++] = repl[0]

        if (repl.size > 1) {
          dst[dp++] = repl[1]
        }

        continue
      }

      if (bb > CharsetMapping.MAX_SINGLE_BYTE) {
        if (mode == Ebcdic.SBCS) {
          mode = Ebcdic.DBCS
          dst[dp++] = Ebcdic.SO.toByte()
        }

        dst[dp++] = (bb shr 8).toByte()
        dst[dp++] = bb.toByte()
      } else {
        if (mode == Ebcdic.DBCS) {
          mode = Ebcdic.SBCS
          dst[dp++] = Ebcdic.SI.toByte()
        }

        dst[dp++] = bb.toByte()
      }
    }

    if (mode == Ebcdic.DBCS) {
      dst[dp++] = Ebcdic.SI.toByte()
    }

    return dst.copyOf(dp)
  }

  override fun setReplacement(newReplacement: ByteArray?) {
    if (newReplacement != null) {
      val size = newReplacement.size
      require(size == 1 || size == 2) {
        "The replacement sequence must be 1 or 2 bytes long"
      }
    }

    replacement = newReplacement
  }

  override fun reset() {
    mode = Ebcdic.SBCS
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

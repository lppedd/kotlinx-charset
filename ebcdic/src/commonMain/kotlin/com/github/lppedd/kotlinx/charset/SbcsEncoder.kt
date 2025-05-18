package com.github.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
internal class SbcsEncoder(
  private val c2b: CharArray,
  private val c2bIndex: CharArray,
) : XCharsetEncoder {
  private var replacement: Byte? = '?'.code.toByte()

  override fun encode(value: String): ByteArray {
    reset()

    val sl = value.length // source length
    val bytes = ByteArray(sl)
    var sp = 0 // source position
    var dp = 0 // destination position

    while (sp < value.length) {
      val ch = value[sp++]
      val byte = encode(ch)

      if (byte != CharsetMapping.UNMAPPABLE_ENCODING) {
        bytes[dp++] = byte.code.toByte()
        continue
      }

      val repl = replOrThrow("Char code ${ch.toHex()} is not mapped to a valid byte")

      // Skip over if it is a surrogate pair
      if (ch.isHighSurrogate() && sp < sl && value[sp].isLowSurrogate()) {
        // Skip low surrogate
        sp++
      }

      bytes[dp++] = repl
    }

    return if (dp == bytes.size) bytes else bytes.copyOf(dp)
  }

  override fun setReplacement(newReplacement: ByteArray?) {
    if (newReplacement != null) {
      require(newReplacement.size == 1) {
        "The replacement sequence must be 1 byte long"
      }

      replacement = newReplacement[0]
    } else {
      replacement = null
    }
  }

  override fun reset() {
    // There is no internal state to reset
  }

  private fun encode(ch: Char): Char {
    val high = ch.code shr 8
    val offset = c2bIndex[high]

    if (offset == CharsetMapping.UNMAPPABLE_ENCODING) {
      return offset
    }

    val low = ch.code and 0xFF
    return c2b[offset.code + low]
  }

  private fun replOrThrow(message: String): Byte {
    val repl = replacement

    if (repl != null) {
      return repl
    }

    throw MessageCharacterCodingException(message)
  }
}

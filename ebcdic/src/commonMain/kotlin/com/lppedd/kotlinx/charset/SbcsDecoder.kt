package com.lppedd.kotlinx.charset

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
      val b = bytes[i].toInt() and 0xFF /* to unsigned */
      val c = b2c[b]

      if (c == CharsetMapping.UNMAPPABLE_DECODING) {
        val repl = replOrThrow("Byte ${b.toHex()} is not mapped to a valid character")
        sb.append(repl)
      } else {
        sb.append(c)
      }
    }

    return sb.toString()
  }

  override fun setReplacement(newReplacement: String?) {
    if (newReplacement != null) {
      require(newReplacement.length == 1) {
        "The replacement sequence must be 1 character long"
      }

      replacement = newReplacement[0]
    } else {
      replacement = null
    }
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

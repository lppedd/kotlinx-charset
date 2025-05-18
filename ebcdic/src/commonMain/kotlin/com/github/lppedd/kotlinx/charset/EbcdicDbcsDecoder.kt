package com.github.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
internal class EbcdicDbcsDecoder(
  private val b2Min: Int,
  private val b2Max: Int,
  private val b2cSB: CharArray,
  private val b2c: Array<CharArray>,
) : XCharsetDecoder {
  // The replacement character in case of malformed or unmappable bytes
  private var replacement: Char? = '\uFFFD'

  // The decoder starts in SBCS mode
  private var mode: Int = Ebcdic.SBCS

  @Suppress("MoveVariableDeclarationIntoWhen")
  override fun decode(bytes: ByteArray): String {
    reset()

    val size = bytes.size
    val sb = StringBuilder(size / 2)
    var i = 0

    while (i < size) {
      val b1 = bytes[i++].toInt() and 0xFF /* to unsigned */

      when (b1) {
        Ebcdic.SO -> if (mode != Ebcdic.SBCS) {
          val c = replOrThrow("Malformed input")
          sb.append(c)
        } else {
          mode = Ebcdic.DBCS
        }

        Ebcdic.SI -> if (mode != Ebcdic.DBCS) {
          val c = replOrThrow("Malformed input")
          sb.append(c)
        } else {
          mode = Ebcdic.SBCS
        }

        else -> {
          var c: Char

          if (mode == Ebcdic.SBCS) {
            c = b2cSB[b1]

            if (c == CharsetMapping.UNMAPPABLE_DECODING) {
              c = replOrThrow("Byte ${b1.toHex()} is not mapped to a valid character")
            }
          } else {
            if (i >= size) {
              // There are no more bytes to read, so the DBCS second byte is missing
              c = replOrThrow("Not enough bytes to read the second byte after ${b1.toHex()}")
            } else {
              val b2 = bytes[i++].toInt() and 0xFF /* to unsigned */

              // DBCS second byte is outside the allowed range
              if (b2 < b2Min || b2 > b2Max) {
                val range = "${b2Min.toHex()}-${b2Max.toHex()}"
                c = replOrThrow("The second byte is outside of the allowed range: $range")
              } else {
                c = b2c[b1][b2 - b2Min]

                if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                  val b1b2 = (b1 shl 8) or (b2 and 0xFF)
                  c = replOrThrow("Bytes ${b1b2.toHex()} are not mapped to a valid character")
                }
              }
            }
          }

          sb.append(c)
        }
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
    mode = Ebcdic.SBCS
  }

  private fun replOrThrow(message: String): Char {
    val repl = replacement

    if (repl != null) {
      return repl
    }

    throw MessageCharacterCodingException(message)
  }
}

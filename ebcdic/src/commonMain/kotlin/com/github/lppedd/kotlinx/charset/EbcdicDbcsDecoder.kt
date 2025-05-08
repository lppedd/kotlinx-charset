package com.github.lppedd.kotlinx.charset

import com.github.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_DECODING

/**
 * @author Edoardo Luppi
 */
internal class EbcdicDbcsDecoder(
  private val b2Min: Int,
  private val b2Max: Int,
  private val b2cSB: CharArray,
  private val b2c: Array<CharArray>,
) : XCharsetDecoder {
  companion object {
    // The SBCS mode identifier
    const val SBCS: Int = 0

    // The DBCS mode identifier
    const val DBCS: Int = 1

    // Shift-Out (switch to DBCS mode)
    const val SO: Int = 0x0E

    // Shift-In (switch to SBCS mode)
    const val SI: Int = 0x0F
  }

  // The replacement character in case of malformed or unmappable bytes
  private var replacement: Char? = '\uFFFD'

  // The decoder starts in SBCS mode
  private var mode: Int = SBCS

  @Suppress("MoveVariableDeclarationIntoWhen")
  override fun decode(bytes: ByteArray): String {
    reset()

    val size = bytes.size
    val sb = StringBuilder(size / 2)
    var i = 0

    while (i < size) {
      val b1 = bytes[i++].toInt() and 0xFF /* to unsigned */

      when (b1) {
        SO -> if (mode != SBCS) {
          val c = replOrThrow("Malformed input")
          sb.append(c)
        } else {
          mode = DBCS
        }

        SI -> if (mode != DBCS) {
          val c = replOrThrow("Malformed input")
          sb.append(c)
        } else {
          mode = SBCS
        }

        else -> {
          var c: Char

          if (mode == SBCS) {
            c = b2cSB[b1]

            if (c == UNMAPPABLE_DECODING) {
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

                if (c == UNMAPPABLE_DECODING) {
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
    mode = SBCS
  }

  private fun replOrThrow(message: String): Char {
    val repl = replacement

    if (repl != null) {
      return repl
    }

    throw MessageCharacterCodingException(message)
  }
}

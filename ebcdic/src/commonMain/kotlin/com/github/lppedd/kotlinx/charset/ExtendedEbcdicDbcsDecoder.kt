package com.github.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
internal class ExtendedEbcdicDbcsDecoder(
  private val b2Min: Int,
  private val b2Max: Int,
  private val b2cSB: CharArray,
  private val b2c: Array<IntArray>,
  private val b2cComposites: Array<out Entry>,
) : XCharsetDecoder {
  // The replacement character in case of malformed or unmappable bytes
  private var replacement: Char? = '\uFFFD'

  // The decoder starts in SBCS mode
  private var mode: Int = Ebcdic.SBCS

  // Used to find the composite character sequence mapped to a certain byte sequence
  private val comparatorBytes: Comparator<Entry> = Comparator { a, b -> a.bs - b.bs }

  override fun decode(bytes: ByteArray): String {
    reset()

    val size = bytes.size
    val sb = StringBuilder(size / 2)
    var i = 0

    while (i < size) {
      when (val b1 = bytes[i++].toInt() and 0xFF /* to unsigned */) {
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
          if (mode == Ebcdic.SBCS) {
            var c = b2cSB[b1]

            if (c == CharsetMapping.UNMAPPABLE_DECODING) {
              c = replOrThrow("Byte ${b1.toHex()} is not mapped to a valid character")
            }

            sb.append(c)
            continue
          }

          if (i >= size) {
            // There are no more bytes to read, so the second byte is missing
            val repl = replOrThrow("Not enough bytes to read the second byte after ${b1.toHex()}")
            sb.append(repl)
            continue
          }

          val b2 = bytes[i++].toInt() and 0xFF /* to unsigned */

          if (b2 < b2Min || b2 > b2Max) {
            // Second byte is outside the allowed range
            val range = "${b2Min.toHex()}-${b2Max.toHex()}"
            val repl = replOrThrow("The second byte is outside of the allowed range: $range")
            sb.append(repl)
            continue
          }

          val c = b2c[b1][b2 - b2Min]

          if (c == CharsetMapping.UNMAPPABLE_DECODING.code) {
            // Check if the byte sequence maps to a composite character sequence
            val bs = (b1 shl 8) or (b2 and 0xFF)
            val cc = findCompositeCharacters(bs)

            if (cc != null) {
              sb.append(cc[0])
              sb.append(cc[1])
            } else {
              val repl = replOrThrow("Bytes ${bs.toHex()} are not mapped to a valid character")
              sb.append(repl)
            }

            continue
          }

          if (c < 0x10000) {
            // The code point is in the BMP plane (0-FFFF)
            sb.append(c.toChar())
          } else {
            // The code point needs to be converted into high and low surrogate pair
            val pair = CodePoint.toChars(c)
            sb.append(pair[0]) // high surrogate
            sb.append(pair[1]) // low surrogate
          }
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

  private fun findCompositeCharacters(bs: Int): CharArray? {
    val i = b2cComposites.binarySearch(Entry(bs = bs), comparatorBytes)

    if (i > -1) {
      val entry = b2cComposites[i]
      val cc = CharArray(2)
      cc[0] = entry.cp
      cc[1] = entry.cp2
      return cc
    }

    return null
  }
}

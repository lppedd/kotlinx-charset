package com.lppedd.kotlinx.charset

// Note: this encoder has been implemented to mimic what would be a
//  JDK implementation behavior. The JDK EBCDIC DBCS implementations
//  do not encode SI/SO before encoding the replacement byte sequence.
//  There is currently an open question on whether this is right or not.

/**
 * @author Edoardo Luppi
 */
internal class ExtendedEbcdicDbcsEncoder(
  private val c2b: CharArray,
  private val c2bIndex: IntArray,
  private val c2bComposites: Array<out Entry>,
) : XCharsetEncoder {
  // The replacement character in case of malformed or unmappable bytes
  private var replacement: ByteArray? = byteArrayOf(0x6F)

  // The encoder starts in SBCS mode
  private var mode: Int = Ebcdic.SBCS

  // Used to search for a composite base character (cp) in c2bComposites
  private val comparatorBase: Comparator<Entry> = Comparator { a, b -> a.cp - b.cp }

  // Used to search for a composite character sequence (cp+cp2) in c2bComposites
  private val comparatorComp: Comparator<Entry> = Comparator { a, b ->
    var r = a.cp - b.cp

    if (r == 0) {
      r = a.cp2 - b.cp2
    }

    r
  }

  // Stores the last found composite base character
  private var leftoverBase: Char = 0.toChar()

  override fun encode(value: String): ByteArray {
    reset()

    val length = value.length
    val dst = ByteArray(length * 4)
    var sp = 0 // source position
    var dp = 0 // destination position

    while (sp < length) {
      val c = value[sp++]

      if (leftoverBase != 0.toChar()) {
        var isComposite = true
        var bs = encodeComposite(leftoverBase, c)

        if (bs == CharsetMapping.UNMAPPABLE_ENCODING_INT) {
          bs = encodeChar(leftoverBase)
          // TODO: should probably check if bs is unmappable
          isComposite = false
        }

        if (mode == Ebcdic.SBCS) {
          mode = Ebcdic.DBCS
          dst[dp++] = Ebcdic.SO.toByte()
        }

        // TODO: we always consider bs to be a double byte sequence, but what
        //  if encodeChar(leftoverBase) returned a single byte?
        dst[dp++] = (bs shr 8).toByte()
        dst[dp++] = bs.toByte()
        leftoverBase = 0.toChar()

        if (isComposite) {
          continue
        }
      }

      if (isCompositeBase(c)) {
        leftoverBase = c
        continue
      }

      var bb = encodeChar(c)

      if (bb <= CharsetMapping.MAX_SINGLE_BYTE) {
        // This is a single byte mapping
        if (mode == Ebcdic.DBCS) {
          mode = Ebcdic.SBCS
          dst[dp++] = Ebcdic.SI.toByte()
        }

        dst[dp++] = bb.toByte()
        continue
      }

      if (bb != CharsetMapping.UNMAPPABLE_ENCODING_INT) {
        // This is a double byte mapping
        if (mode == Ebcdic.SBCS) {
          mode = Ebcdic.DBCS
          dst[dp++] = Ebcdic.SO.toByte()
        }

        dst[dp++] = (bb shr 8).toByte()
        dst[dp++] = bb.toByte()
        continue
      }

      if (c.isHighSurrogate()) {
        if (mode == Ebcdic.SBCS) {
          mode = Ebcdic.DBCS
          dst[dp++] = Ebcdic.SO.toByte()
        }

        // Check if this is a valid high-low surrogate pair
        val c2 = value[sp++]

        if (!c2.isLowSurrogate()) {
          // Not a valid surrogate pair
          dp = appendReplacementOrThrow(dst, dp) {
            "Character ${c2.toHex()} is not a low surrogate code unit"
          }

          continue
        }

        bb = encodeSurrogate(c, c2)

        if (bb == CharsetMapping.UNMAPPABLE_ENCODING_INT) {
          // Unmapped Unicode code point
          dp = appendReplacementOrThrow(dst, dp) {
            "The surrogate pair ${c.toHex()}+${c2.toHex()} is not mapped to a valid byte sequence"
          }

          continue
        }

        dst[dp++] = (bb shr 8).toByte()
        dst[dp++] = bb.toByte()
        continue
      }

      if (c.isLowSurrogate()) {
        // There should have been a high surrogate character before this one
        dp = appendReplacementOrThrow(dst, dp) {
          "The low surrogate code unit ${c.toHex()} is not preceded by an high surrogate code unit"
        }

        continue
      }

      // No previous condition have applied, so the only choice
      // we have is to throw or use the replacement sequence
      dp = appendReplacementOrThrow(dst, dp) {
        "Character ${c.toHex()} is not mapped to a valid byte sequence"
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
    leftoverBase = 0.toChar()
  }

  private fun appendReplacementOrThrow(dst: ByteArray, dstIndex: Int, message: () -> String): Int {
    val repl = replacement ?: throw MessageCharacterCodingException(message())
    var i = dstIndex

    when (mode) {
      Ebcdic.DBCS -> if (repl.size == 1) {
        dst[i++] = Ebcdic.SI.toByte()
        mode = Ebcdic.SBCS
      }
      Ebcdic.SBCS -> if (repl.size > 1) {
        dst[i++] = Ebcdic.SO.toByte()
        mode = Ebcdic.DBCS
      }
    }

    dst[i++] = repl[0]

    if (repl.size > 1) {
      dst[i++] = repl[1]
    }

    return i
  }

  private fun isCompositeBase(cp: Char): Boolean {
    if (cp in '\u00E6'..'\u31F7') {
      val i = c2bComposites.binarySearch(Entry(cp = cp), comparatorBase)
      return i > -1
    }

    return false
  }

  private fun encodeComposite(cp: Char, cp2: Char): Int {
    val i = c2bComposites.binarySearch(Entry(cp = cp, cp2 = cp2), comparatorComp)

    if (i > -1) {
      return c2bComposites[i].bs
    }

    return CharsetMapping.UNMAPPABLE_ENCODING_INT
  }

  private fun encodeSurrogate(high: Char, low: Char): Int {
    val cp = CodePoint.toCodePoint(high, low)
    return encodeCodePoint(cp)
  }

  private fun encodeChar(ch: Char): Int {
    val cp = ch.code
    return encodeCodePoint(cp)
  }

  private fun encodeCodePoint(cp: Int): Int {
    val highBits = cp shr 8
    val lowBits = cp and 0xFF
    val index = c2bIndex[highBits]
    val bytes = c2b[index + lowBits]
    return bytes.code
  }
}

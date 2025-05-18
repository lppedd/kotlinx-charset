package com.github.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
internal object ExtendedDbcs {
  fun initC2B(
    b2c: Array<IntArray?>,
    b2cSB: CharArray,
    b2cNR: IntArray,
    c2bNR: IntArray,
    b2Min: Int,
    b2Max: Int,
    c2b: CharArray,
    c2bIndex: IntArray,
  ) {
    // Reset the c2b table
    c2b.fill(CharsetMapping.UNMAPPABLE_ENCODING)

    // Leave room for a shared unmappable segment at c2bIndex index 0
    var offset = 256

    // If any, apply the non-roundtrip .nr entries.
    // b2cNR contains byte-char pairs that should not reverse-map
    // back to bytes, so we set UNMAPPABLE_DECODING to the appropriate
    // indexes in b2cSB (single byte) and b2c (double byte).
    var j = 0
    while (j < b2cNR.size) {
      val b = b2cNR[j++]
      val c = b2cNR[j++]

      if (b <= 0xFF && b2cSB.isNotEmpty()) {
        if (b2cSB[b] == c.toChar()) {
          b2cSB[b] = CharsetMapping.UNMAPPABLE_DECODING
        }
      } else {
        val b2cRow = b2c[b shr 8]
        val i = (b and 0xFF) - b2Min

        if (b2cRow != null && b2cRow[i] == c) {
          b2cRow[i] = CharsetMapping.UNMAPPABLE_DECODING_INT
        }
      }
    }

    // Process the single byte table.
    // Map a Unicode character to its single byte value.
    for (b in b2cSB.indices) {
      val c = b2cSB[b].code

      if (c == CharsetMapping.UNMAPPABLE_DECODING_INT) {
        continue
      }

      var index = c2bIndex[c shr 8]

      if (index == 0) {
        index = offset
        offset += 256
        c2bIndex[c shr 8] = index
      }

      // index + (c and 0xFF) means:
      //   index is the base offset in the flattened c2b array (the row index)
      //   c and 0xFF is the low byte of the character (the column index in the flattened c2b row)
      c2b[index + (c and 0xFF)] = b.toChar()
    }

    // Process the double byte table.
    // Map a Unicode character to its double byte value.
    for (b1 in b2c.indices) {
      val b2cRow = b2c[b1] ?: continue

      for (b2 in b2Min..b2Max) {
        val c = b2cRow[b2 - b2Min]

        if (c == CharsetMapping.UNMAPPABLE_DECODING_INT) {
          continue
        }

        var index = c2bIndex[c shr 8]

        if (index == 0) {
          index = offset
          offset += 256
          c2bIndex[c shr 8] = index
        }

        // index + (c and 0xFF) means:
        //   index is the base offset in the flattened c2b array (the row index)
        //   c and 0xFF is the low byte of the character (the column index in the flattened c2b row)
        //
        // (b1 shl 8) or b2 combines b1 and b2 into a single int:
        //   b1 = 0x41
        //   b2 = 0x42
        //   result = 0x4142
        c2b[index + (c and 0xFF)] = ((b1 shl 8) or b2).toChar()
      }
    }

    // If any (non-empty string), apply the non-roundtrip .c2b entries.
    // These are basically extra character to byte(s) mappings,
    // that do not appear in the byte(s) to character tables.
    j = 0
    while (j < c2bNR.size) {
      val b = c2bNR[j++]
      val c = c2bNR[j++]
      var index = (c shr 8)

      if (c2bIndex[index] == 0) {
        c2bIndex[index] = offset
        offset += 256
      }

      index = c2bIndex[index] + (c and 0xFF)
      c2b[index] = b.toChar()
    }
  }
}

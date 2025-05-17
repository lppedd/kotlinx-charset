package com.github.lppedd.kotlinx.charset

import com.github.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_DECODING
import com.github.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_ENCODING

/**
 * @author Edoardo Luppi
 */
internal object Dbcs {
  fun initC2B(
    b2c: Array<String?>,
    b2cSB: String,
    b2cNR: String,
    c2bNR: String,
    b2Min: Int,
    b2Max: Int,
    c2b: CharArray,
    c2bIndex: CharArray,
  ) {
    // Reset the c2b table
    c2b.fill(UNMAPPABLE_ENCODING)

    // Leave room for a shared unmappable segment at c2bIndex index 0
    var offset = 256
    val b2cCA = Array(b2c.size) {
      val s = b2c[it]
      s?.toCharArray()
    }

    val b2cSBCA = b2cSB.toCharArray()

    // If any (non-empty string), apply the non-roundtrip .nr entries.
    // b2cNR contains byte-char pairs that should not reverse-map
    // back to bytes, so we set UNMAPPABLE_DECODING to the appropriate
    // indexes in b2cSBCA (single byte) and b2cCA (double byte).
    var j = 0

    while (j < b2cNR.length) {
      val b = b2cNR[j++].code
      val c = b2cNR[j++]

      if (b < 256 && b2cSBCA.isNotEmpty()) {
        if (b2cSBCA[b] == c) {
          b2cSBCA[b] = UNMAPPABLE_DECODING
        }
      } else {
        val chars = b2cCA[b shr 8]
        val i = (b and 0xFF) - b2Min

        if (chars != null && chars[i] == c) {
          chars[i] = UNMAPPABLE_DECODING
        }
      }
    }

    // If present, process the single byte table.
    // Here we map a Unicode character to its single byte value.
    for (b in b2cSBCA.indices) {
      val c = b2cSBCA[b].code

      if (c == UNMAPPABLE_DECODING.code) {
        continue
      }

      var index = c2bIndex[c shr 8].code

      if (index == 0) {
        index = offset
        offset += 256
        c2bIndex[c shr 8] = index.toChar()
      }

      // index + (c and 0xFF) means:
      //   index is the base offset in the flattened c2b array (the row index)
      //   c and 0xFF is the low byte of the character (the column index in the flattened c2b row)
      c2b[index + (c and 0xFF)] = b.toChar()
    }

    // Process the double byte table.
    // Here we map a Unicode character to its double byte value.
    for (b1 in b2c.indices) {
      val db = b2cCA[b1] ?: continue

      for (b2 in b2Min..b2Max) {
        val c = db[b2 - b2Min].code

        if (c == UNMAPPABLE_DECODING.code) {
          continue
        }

        var index = c2bIndex[c shr 8].code

        if (index == 0) {
          index = offset
          offset += 256
          c2bIndex[c shr 8] = index.toChar()
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
    for (i in c2bNR.indices step 2) {
      val b = c2bNR[i]
      val c = c2bNR[i + 1].code
      var index = (c shr 8)

      if (c2bIndex[index].code == 0) {
        c2bIndex[index] = offset.toChar()
        offset += 256
      }

      index = c2bIndex[index].code + (c and 0xFF)
      c2b[index] = b
    }
  }
}

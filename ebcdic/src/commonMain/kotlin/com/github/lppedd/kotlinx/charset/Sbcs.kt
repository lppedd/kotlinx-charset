package com.github.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
internal object Sbcs {
  fun initC2B(b2c: CharArray, c2bNR: CharArray, c2b: CharArray, c2bIndex: CharArray) {
    c2bIndex.fill(CharsetMapping.UNMAPPABLE_ENCODING)
    c2b.fill(CharsetMapping.UNMAPPABLE_ENCODING)

    var offset = 0
    var i = 0

    while (i < b2c.size) {
      val c = b2c[i]

      if (c == CharsetMapping.UNMAPPABLE_DECODING) {
        i++
        continue
      }

      val charCode = c.code
      val high = charCode shr 8
      val low = charCode and 0xFF

      if (c2bIndex[high] == CharsetMapping.UNMAPPABLE_ENCODING) {
        c2bIndex[high] = offset.toChar()
        offset += 256
      }

      val off = c2bIndex[high]
      c2b[off.code + low] = i.toChar()
      i++
    }

    // c -> b .nr entries
    i = 0
    while (i < c2bNR.size) {
      val b = c2bNR[i++]
      val c = c2bNR[i++]

      val charCode = c.code
      val high = charCode shr 8
      val low = charCode and 0xFF

      if (c2bIndex[high] == CharsetMapping.UNMAPPABLE_ENCODING) {
        c2bIndex[high] = offset.toChar()
        offset += 256
      }

      val off = c2bIndex[high]
      c2b[off.code + low] = b
    }
  }
}

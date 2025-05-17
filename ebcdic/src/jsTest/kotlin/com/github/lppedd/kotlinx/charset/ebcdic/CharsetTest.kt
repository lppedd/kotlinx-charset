package com.github.lppedd.kotlinx.charset.ebcdic

import com.github.lppedd.kotlinx.charset.XCharset
import com.github.lppedd.kotlinx.charset.decodeToHexString
import com.github.lppedd.kotlinx.charset.readResourceText
import com.github.lppedd.kotlinx.charset.toHexString
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
abstract class CharsetTest(
  private val charset: XCharset,
  private val b2cFile: String,
) {
  private val wsRegex = Regex("\\s+")
  private val byteHexRegex = Regex("^[0-9A-F]+$")
  private val charHexRegex = Regex("^[0-9A-F]{4}$")

  @Test
  fun decodeFromB2CFile() {
    val b2cMappings = readResourceText(b2cFile).trim()
    val bytes = Buffer()
    val chars = ArrayList<Char>()

    for (line in b2cMappings.lines()) {
      val (bs, c) = parseB2CLine(line)
      bytes.write(bs)
      chars.add(c)
    }

    val expectedHex = chars.toCharArray().toHexString()
    val hex = charset.decodeToHexString(bytes.readByteArray())
    assertEquals(expectedHex, hex)
  }

  private fun parseB2CLine(line: String): Pair<ByteArray, Char> {
    val (byteHex, charHex) = line.split(wsRegex)
    val ucByteHex = byteHex.uppercase()
    val ucCharHex = charHex.uppercase()
    check(ucByteHex.matches(byteHexRegex)) { "Invalid single byte: $ucByteHex" }
    check(ucCharHex.matches(charHexRegex)) { "Invalid single byte character: $ucCharHex" }

    val bs = ucByteHex.hexToByteArray(HexFormat.UpperCase)
    val cp = ucCharHex.hexToInt(HexFormat.UpperCase)

    // Check the code point is in the BMP
    check(cp < 0x10000)
    return bs to cp.toChar()
  }
}

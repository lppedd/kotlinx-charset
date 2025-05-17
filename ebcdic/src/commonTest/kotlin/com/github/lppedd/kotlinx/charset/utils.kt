package com.github.lppedd.kotlinx.charset

@OptIn(ExperimentalStdlibApi::class)
private val encodeFormat = HexFormat {
  upperCase = true
  bytes {
    byteSeparator = ","
  }
}

internal fun XCharset.decodeToCharArray(bytes: ByteArray, replacement: String? = ""): CharArray {
  val decoder = this.newDecoder()

  if (replacement == null || replacement.isNotEmpty()) {
    decoder.setReplacement(replacement)
  }

  val str = decoder.decode(bytes)
  return str.toCharArray()
}

@OptIn(ExperimentalStdlibApi::class)
internal fun XCharset.encodeToHexString(str: String, replacement: ByteArray? = byteArrayOf()): String {
  val encoder = this.newEncoder()

  if (replacement == null || replacement.isNotEmpty()) {
    encoder.setReplacement(replacement)
  }

  val bytes = encoder.encode(str)
  return bytes.toHexString(encodeFormat)
}

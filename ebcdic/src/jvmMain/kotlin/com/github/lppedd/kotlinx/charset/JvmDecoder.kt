package com.github.lppedd.kotlinx.charset

import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction

/**
 * @author Edoardo Luppi
 */
internal class JvmDecoder(private val nativeDecoder: java.nio.charset.CharsetDecoder) : XCharsetDecoder {
  override fun decode(bytes: ByteArray): String {
    if (bytes.isEmpty()) {
      return ""
    }

    val byteBuffer = ByteBuffer.wrap(bytes)
    val charBuffer = nativeDecoder.decode(byteBuffer)
    return charBuffer.toString()
  }

  override fun withReplacement(newReplacement: String?): XCharsetDecoder {
    if (newReplacement != null) {
      nativeDecoder.replaceWith(newReplacement)
      nativeDecoder.onMalformedInput(CodingErrorAction.REPLACE)
      nativeDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    } else {
      nativeDecoder.onMalformedInput(CodingErrorAction.REPORT)
      nativeDecoder.onUnmappableCharacter(CodingErrorAction.REPORT)
    }

    return this
  }

  override fun reset() {
    nativeDecoder.reset()
  }
}

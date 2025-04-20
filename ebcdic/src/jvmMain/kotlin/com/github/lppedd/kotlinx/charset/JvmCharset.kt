package com.github.lppedd.kotlinx.charset

import java.nio.charset.CodingErrorAction

/**
 * @author Edoardo Luppi
 */
public abstract class JvmCharset(private val jvmCharset: java.nio.charset.Charset) : XCharset {
  override val name: String =
    jvmCharset.name()

  override val aliases: Array<String>
    get() = jvmCharset.aliases().toTypedArray()

  override fun newDecoder(): XCharsetDecoder {
    val decoder = jvmCharset.newDecoder()
    decoder.onMalformedInput(CodingErrorAction.REPLACE)
    decoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    return JvmDecoder(decoder)
  }

  override fun newEncoder(): XCharsetEncoder {
    val encoder = jvmCharset.newEncoder()
    encoder.onMalformedInput(CodingErrorAction.REPLACE)
    encoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    return JvmEncoder(encoder)
  }
}

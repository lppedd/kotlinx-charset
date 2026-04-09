// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

import java.nio.charset.CodingErrorAction

/**
 * @author Edoardo Luppi
 */
public abstract class JvmCharset(private val nioCharset: java.nio.charset.Charset) : XCharset {
  override val name: String =
    nioCharset.name()

  override val aliases: Array<String>
    get() = nioCharset.aliases().toTypedArray()

  override fun newDecoder(): XCharsetDecoder {
    val decoder = nioCharset.newDecoder()
    decoder.onMalformedInput(CodingErrorAction.REPLACE)
    decoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    return JvmDecoder(decoder)
  }

  override fun newEncoder(): XCharsetEncoder {
    val encoder = nioCharset.newEncoder()
    encoder.onMalformedInput(CodingErrorAction.REPLACE)
    encoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    return JvmEncoder(encoder)
  }

  override fun toString(): String =
    "(JvmCharset) $nioCharset"
}

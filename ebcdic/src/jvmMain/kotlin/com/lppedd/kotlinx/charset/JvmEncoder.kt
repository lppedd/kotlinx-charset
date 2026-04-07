// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

import java.nio.CharBuffer
import java.nio.charset.CodingErrorAction

/**
 * @author Edoardo Luppi
 */
internal class JvmEncoder(private val nioEncoder: java.nio.charset.CharsetEncoder) : XCharsetEncoder {
  override fun encode(value: String): ByteArray {
    if (value.isEmpty()) {
      return ByteArray(0)
    }

    val charBuffer = CharBuffer.wrap(value)
    val byteBuffer = nioEncoder.encode(charBuffer)
    val byteArray = ByteArray(byteBuffer.remaining())
    byteBuffer.get(byteArray)
    return byteArray
  }

  override fun setReplacement(newReplacement: ByteArray?) {
    if (newReplacement != null) {
      nioEncoder.replaceWith(newReplacement)
      nioEncoder.onMalformedInput(CodingErrorAction.REPLACE)
      nioEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    } else {
      nioEncoder.onMalformedInput(CodingErrorAction.REPORT)
      nioEncoder.onUnmappableCharacter(CodingErrorAction.REPORT)
    }
  }

  override fun reset() {
    nioEncoder.reset()
  }
}

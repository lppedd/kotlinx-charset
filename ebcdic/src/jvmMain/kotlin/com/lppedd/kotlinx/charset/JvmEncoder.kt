// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

import java.nio.CharBuffer
import java.nio.charset.CodingErrorAction

/**
 * @author Edoardo Luppi
 */
internal class JvmEncoder(private val nativeEncoder: java.nio.charset.CharsetEncoder) : XCharsetEncoder {
  override fun encode(value: String): ByteArray {
    if (value.isEmpty()) {
      return ByteArray(0)
    }

    val charBuffer = CharBuffer.wrap(value)
    val byteBuffer = nativeEncoder.encode(charBuffer)
    val byteArray = ByteArray(byteBuffer.remaining())
    byteBuffer.get(byteArray)
    return byteArray
  }

  override fun setReplacement(newReplacement: ByteArray?) {
    if (newReplacement != null) {
      nativeEncoder.replaceWith(newReplacement)
      nativeEncoder.onMalformedInput(CodingErrorAction.REPLACE)
      nativeEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    } else {
      nativeEncoder.onMalformedInput(CodingErrorAction.REPORT)
      nativeEncoder.onUnmappableCharacter(CodingErrorAction.REPORT)
    }
  }

  override fun reset() {
    nativeEncoder.reset()
  }
}

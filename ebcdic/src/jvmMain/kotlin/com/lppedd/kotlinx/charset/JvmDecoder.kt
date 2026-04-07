// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction

/**
 * @author Edoardo Luppi
 */
internal class JvmDecoder(private val nioDecoder: java.nio.charset.CharsetDecoder) : XCharsetDecoder {
  override fun decode(bytes: ByteArray): String {
    if (bytes.isEmpty()) {
      return ""
    }

    val byteBuffer = ByteBuffer.wrap(bytes)
    val charBuffer = nioDecoder.decode(byteBuffer)
    return charBuffer.toString()
  }

  override fun setReplacement(newReplacement: String?) {
    if (newReplacement != null) {
      nioDecoder.replaceWith(newReplacement)
      nioDecoder.onMalformedInput(CodingErrorAction.REPLACE)
      nioDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    } else {
      nioDecoder.onMalformedInput(CodingErrorAction.REPORT)
      nioDecoder.onUnmappableCharacter(CodingErrorAction.REPORT)
    }
  }

  override fun reset() {
    nioDecoder.reset()
  }
}

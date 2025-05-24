// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

import org.khronos.webgl.Uint8Array
import com.lppedd.kotlinx.charset.XCharsetEncoder as CoreCharsetEncoder

/**
 * @author Edoardo Luppi
 */
internal class DelegatingCharsetEncoder(private val delegate: CoreCharsetEncoder) : XCharsetEncoder {
  override fun encode(value: String): Uint8Array =
    delegate.encode(value).toUint8Array()

  override fun setReplacement(newReplacement: Uint8Array?) {
    delegate.setReplacement(newReplacement?.toByteArray())
  }

  override fun reset() {
    delegate.reset()
  }

  override fun toString(): String =
    delegate.toString()
}

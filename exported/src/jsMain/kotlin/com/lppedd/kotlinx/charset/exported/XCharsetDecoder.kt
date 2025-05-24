// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

import org.khronos.webgl.Uint8Array

/**
 * @author Edoardo Luppi
 */
@JsExport
public interface XCharsetDecoder {
  public fun decode(bytes: Uint8Array): String
  public fun setReplacement(newReplacement: String?)
  public fun reset()
}

// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

/**
 * @author Edoardo Luppi
 */
@JsExport
public interface XCharsetEncoder {
  public fun encode(value: String): Uint8Array
  public fun setReplacement(newReplacement: Uint8Array?)
  public fun reset()
}

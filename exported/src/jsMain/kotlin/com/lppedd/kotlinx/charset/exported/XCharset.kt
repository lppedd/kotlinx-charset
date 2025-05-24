// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

/**
 * @author Edoardo Luppi
 */
@JsExport
public interface XCharset {
  public val name: String
  public val aliases: Array<String>
  public fun newDecoder(): XCharsetDecoder
  public fun newEncoder(): XCharsetEncoder
}

// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 * @see XCharsetRegistrar.getCharset
 * @see XCharsetRegistrar.registerCharset
 */
public interface XCharset {
  /**
   * Returns the charset's canonical name.
   */
  public val name: String

  /**
   * Returns an array containing the charset's aliases.
   */
  public val aliases: Array<String>

  /**
   * Returns a new decoder for this charset.
   *
   * It is **not** guaranteed the decoder is stateless.
   */
  public fun newDecoder(): XCharsetDecoder

  /**
   * Returns a new encoder for this charset.
   *
   * It is **not** guaranteed the encoder is stateless.
   */
  public fun newEncoder(): XCharsetEncoder
}

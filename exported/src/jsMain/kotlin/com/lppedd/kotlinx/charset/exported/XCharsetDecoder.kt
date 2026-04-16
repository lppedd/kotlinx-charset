// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

/**
 * @author Edoardo Luppi
 */
@JsExport
public interface XCharsetDecoder {
  /**
   * Decodes the inputted bytes into a new string.
   *
   * This method implements an entire decoding operation, that is,
   * it resets this decoder, then it decodes the bytes to a new
   * string, and finally it flushes this decoder.
   */
  public fun decode(bytes: Uint8Array): String

  /**
   * Sets the replacement character(s) for malformed or unmappable bytes.
   *
   * If set to `null`, a decoding error will throw a `CharacterCodingException`.
   */
  public fun setReplacement(newReplacement: String?)

  /**
   * Resets the internal state of the decoder.
   *
   * It does not reset the replacement character(s).
   */
  public fun reset()
}

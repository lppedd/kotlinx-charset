// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

import org.khronos.webgl.Uint8Array

/**
 * @author Edoardo Luppi
 */
@JsExport
public interface XCharsetEncoder {
  /**
   * Encodes the inputted string into a new byte sequence.
   *
   * This method implements an entire encoding operation, that is,
   * it resets this encoder, then it encodes the characters in a
   * new byte array, and finally it flushes this encoder.
   */
  public fun encode(value: String): Uint8Array

  /**
   * Sets the replacement byte(s) for malformed or unmappable characters.
   *
   * If set to `null`, an encoding error will throw a `CharacterCodingException`.
   */
  public fun setReplacement(newReplacement: Uint8Array?)

  /**
   * Resets the internal state of the encoder.
   *
   * It does not reset the replacement byte(s).
   */
  public fun reset()
}

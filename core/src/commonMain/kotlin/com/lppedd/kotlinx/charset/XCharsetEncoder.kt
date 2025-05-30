// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
public interface XCharsetEncoder {
  /**
   * Encodes the inputted string into a new byte sequence.
   *
   * This method implements an entire encoding operation, that is,
   * it resets this encoder, then it encodes the characters in a
   * new byte array, and finally it flushes this encoder.
   *
   * This method should not be invoked if an encoding operation
   * is already in progress.
   */
  public fun encode(value: String): ByteArray

  /**
   * Sets the replacement byte(s) for malformed or unmappable characters.
   *
   * If set to `null`, an encoding error will throw a [CharacterCodingException].
   */
  public fun setReplacement(newReplacement: ByteArray?)

  /**
   * Resets the internal state of the encoder.
   *
   * It does not reset the replacement byte(s).
   */
  public fun reset()
}

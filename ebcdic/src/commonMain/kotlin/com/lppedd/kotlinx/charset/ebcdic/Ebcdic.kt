// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ebcdic

// Note: an object which contains constants only is always inlined in K/JS,
//  so the Ebcdic class acts purely as namespace

/**
 * @author Edoardo Luppi
 */
public object Ebcdic {
  /**
   * Shift-Out (switch to DBCS mode).
   */
  public const val SO: Byte = 0x0E.toByte()

  /**
   * Shift-In (switch to SBCS mode).
   */
  public const val SI: Byte = 0x0F.toByte()

  /**
   * The SBCS mode identifier.
   */
  internal const val SBCS: Int = 0

  /**
   * The DBCS mode identifier.
   */
  internal const val DBCS: Int = 1
}

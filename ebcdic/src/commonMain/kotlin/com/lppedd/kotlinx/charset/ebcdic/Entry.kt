// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ebcdic

import kotlin.jvm.JvmField

/**
 * Represents a mapping to/from a composite character sequence, e.g. `0xECC2 U+31F7+309A`.
 *
 * @author Edoardo Luppi
 */
internal class Entry(
  @JvmField var bs: Int = 0,
  @JvmField var cp: Char = 0.toChar(),
  @JvmField var cp2: Char = 0.toChar(),
)

// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ebcdic

import com.lppedd.kotlinx.charset.decodeToString
import com.lppedd.kotlinx.charset.encodeToBytes
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("SpellCheckingInspection")
class IBM278Test {
  @Test
  fun encodeAndDecodeString() {
    val str = "Det blåa huset låg vid sjön, och Anders sade: 'Åh nej, jag glömde nycklarna på köksbänken!'"
    val encodedBytes = IBM278.encodeToBytes(str)
    val decodedStr = IBM278.decodeToString(encodedBytes)
    assertEquals(str, decodedStr)
  }
}

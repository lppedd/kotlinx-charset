// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

import com.lppedd.kotlinx.charset.ebcdic.provideCharsets
import kotlin.test.Test
import kotlin.test.assertEquals

class EbcdicProviderTest {
  @Test
  fun provideCharsets() {
    val registrar = XCharsetRegistrar()
    provideCharsets(registrar)

    val charsets = registrar.getCharsets()
    assertEquals(19, charsets.size)
  }
}

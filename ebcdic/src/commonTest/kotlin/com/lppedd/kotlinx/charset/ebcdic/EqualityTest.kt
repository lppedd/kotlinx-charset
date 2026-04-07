// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ebcdic

import com.lppedd.kotlinx.charset.XCharsetRegistrar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EqualityTest {
  @Test
  fun ensureCharsetEquality() {
    val registrar = XCharsetRegistrar()
    provideCharsets(registrar)

    assertEquals(registrar.getCharset("IBM037"), registrar.getCharset("IBM037"))
    assertEquals(registrar.getCharset("IBM273"), registrar.getCharset("IBM273"))
    assertEquals(registrar.getCharset("IBM277"), registrar.getCharset("IBM277"))
    assertEquals(registrar.getCharset("IBM278"), registrar.getCharset("IBM278"))
    assertEquals(registrar.getCharset("IBM280"), registrar.getCharset("IBM280"))
    assertEquals(registrar.getCharset("IBM285"), registrar.getCharset("IBM285"))
    assertEquals(registrar.getCharset("IBM297"), registrar.getCharset("IBM297"))
    assertEquals(registrar.getCharset("IBM1047"), registrar.getCharset("IBM1047"))
    assertEquals(registrar.getCharset("IBM1141"), registrar.getCharset("IBM1141"))
    assertEquals(registrar.getCharset("IBM1142"), registrar.getCharset("IBM1142"))
    assertEquals(registrar.getCharset("IBM1143"), registrar.getCharset("IBM1143"))
    assertEquals(registrar.getCharset("IBM1144"), registrar.getCharset("IBM1144"))
    assertEquals(registrar.getCharset("IBM1146"), registrar.getCharset("IBM1146"))
    assertEquals(registrar.getCharset("IBM1147"), registrar.getCharset("IBM1147"))
    assertEquals(registrar.getCharset("IBM930"), registrar.getCharset("IBM930"))
    assertEquals(registrar.getCharset("IBM937"), registrar.getCharset("IBM937"))
    assertEquals(registrar.getCharset("IBM939"), registrar.getCharset("IBM939"))
    assertEquals(registrar.getCharset("IBM1390"), registrar.getCharset("IBM1390"))
    assertEquals(registrar.getCharset("IBM1399"), registrar.getCharset("IBM1399"))

    // Just a couple of inequality tests to be sure the mechanism works
    assertNotEquals(registrar.getCharset("IBM037"), registrar.getCharset("IBM273"))
    assertNotEquals(registrar.getCharset("IBM037"), registrar.getCharset("IBM1047"))
    assertNotEquals(registrar.getCharset("IBM280"), registrar.getCharset("IBM297"))
    assertNotEquals(registrar.getCharset("IBM939"), registrar.getCharset("IBM297"))
    assertNotEquals(registrar.getCharset("IBM939"), registrar.getCharset("IBM930"))
    assertNotEquals(registrar.getCharset("IBM1390"), registrar.getCharset("IBM1399"))
  }
}

// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ucm

internal val bsPrintFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 2
  }
}

internal val cpPrintFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 4
  }
}

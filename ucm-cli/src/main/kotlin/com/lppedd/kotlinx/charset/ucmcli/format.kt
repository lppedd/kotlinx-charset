// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ucmcli

@OptIn(ExperimentalStdlibApi::class)
internal val bsPrintFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 2
  }
}

@OptIn(ExperimentalStdlibApi::class)
internal val cpPrintFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 4
  }
}

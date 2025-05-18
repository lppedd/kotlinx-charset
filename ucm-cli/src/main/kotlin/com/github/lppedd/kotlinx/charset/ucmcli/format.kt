package com.github.lppedd.kotlinx.charset.ucmcli

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

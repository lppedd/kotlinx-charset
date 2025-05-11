package com.github.lppedd.kotlinx.charset.ucm

import kotlin.jvm.JvmSynthetic

@JvmSynthetic
@OptIn(ExperimentalStdlibApi::class)
internal val bsPrintFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 2
  }
}

@JvmSynthetic
@OptIn(ExperimentalStdlibApi::class)
internal val cpPrintFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 4
  }
}

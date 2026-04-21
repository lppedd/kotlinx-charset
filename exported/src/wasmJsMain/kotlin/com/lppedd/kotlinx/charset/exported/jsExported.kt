// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

import com.lppedd.kotlinx.charset.XCharsetRegistrar
import com.lppedd.kotlinx.charset.ebcdic.provideCharsets as provideEbcdicCharsets

@OptIn(ExperimentalStdlibApi::class)
private val registrar = initCharsetRegistrar()

@JsExport
public fun decode(charsetName: String, bytes: Uint8Array): String {
  val charset = registrar.getCharset(charsetName)
  val decoder = charset.newDecoder()
  val byteArray = bytes.toByteArray()
  return decoder.decode(byteArray)
}

@JsExport
public fun encode(charsetName: String, value: String): Uint8Array {
  val charset = registrar.getCharset(charsetName)
  val encoder = charset.newEncoder()
  val byteArray = encoder.encode(value)
  return byteArray.toUint8Array()
}

private fun initCharsetRegistrar(): XCharsetRegistrar {
  val registrar = XCharsetRegistrar()
  provideEbcdicCharsets(registrar)
  return registrar
}

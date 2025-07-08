// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

import com.lppedd.kotlinx.charset.XCharsetRegistrar
import org.khronos.webgl.Uint8Array
import com.lppedd.kotlinx.charset.ebcdic.provideCharsets as provideEbcdicCharsets

@Suppress("DEPRECATION")
@OptIn(ExperimentalStdlibApi::class)
@EagerInitialization
private val registrar = initCharsetRegistrar()

@JsExport
public fun getCharsetOrNull(charsetName: String): XCharset? {
  val delegate = registrar.getCharsetOrNull(charsetName)
  return if (delegate != null) DelegatingCharset(delegate) else null
}

@JsExport
public fun getCharset(charsetName: String): XCharset {
  val delegate = registrar.getCharset(charsetName)
  return DelegatingCharset(delegate)
}

@JsExport
public fun getCharsets(): Array<XCharset> {
  val charsets = registrar.getCharsets()
  return Array(charsets.size) {
    DelegatingCharset(charsets[it])
  }
}

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

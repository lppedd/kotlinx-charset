// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
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
public external interface DecodeOptions {
  /**
   * The replacement character(s) for malformed or unmappable bytes.
   *
   * If set to `null`, a decoding error will throw a `CharacterCodingException`.
   */
  public val replacement: String?
}

@JsExport
public external interface EncodeOptions {
  /**
   * The replacement byte(s) for malformed or unmappable characters.
   *
   * If set to `null`, an encoding error will throw a `CharacterCodingException`.
   */
  public val replacement: Uint8Array?
}

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
public fun decode(charsetName: String, bytes: Uint8Array, options: DecodeOptions? = null): String {
  val charset = registrar.getCharset(charsetName)
  val decoder = charset.newDecoder()

  if (options != null && isNotUndefined(options, "replacement")) {
    decoder.setReplacement(options.replacement)
  }

  val byteArray = bytes.toByteArray()
  return decoder.decode(byteArray)
}

@JsExport
public fun encode(charsetName: String, value: String, options: EncodeOptions? = null): Uint8Array {
  val charset = registrar.getCharset(charsetName)
  val encoder = charset.newEncoder()

  if (options != null && isNotUndefined(options, "replacement")) {
    encoder.setReplacement(options.replacement?.toByteArray())
  }

  val byteArray = encoder.encode(value)
  return byteArray.toUint8Array()
}

private fun initCharsetRegistrar(): XCharsetRegistrar {
  val registrar = XCharsetRegistrar()
  provideEbcdicCharsets(registrar)
  return registrar
}

private fun isNotUndefined(@Suppress("unused") value: Any, @Suppress("unused") prop: String): Boolean =
  js("typeof value[prop] !== 'undefined'")

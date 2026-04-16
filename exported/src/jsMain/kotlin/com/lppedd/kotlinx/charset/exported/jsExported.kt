// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

import com.lppedd.kotlinx.charset.XCharsetRegistrar
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

/**
 * Returns the charset with the specified [charsetName], or `null` if it is not available.
 *
 * @param charsetName The canonical name or alias of the charset
 */
@JsExport
public fun getCharsetOrNull(charsetName: String): XCharset? {
  val delegate = registrar.getCharsetOrNull(charsetName)
  return if (delegate != null) DelegatingCharset(delegate) else null
}

/**
 * Returns the charset for the specified [charsetName].
 *
 * Throws if the charset is not available.
 *
 * @param charsetName The canonical name or alias of the charset
 */
@JsExport
public fun getCharset(charsetName: String): XCharset {
  val delegate = registrar.getCharset(charsetName)
  return DelegatingCharset(delegate)
}

/**
 * Returns all available charset instances.
 */
@JsExport
public fun getCharsets(): Array<XCharset> {
  val charsets = registrar.getCharsets()
  return Array(charsets.size) {
    DelegatingCharset(charsets[it])
  }
}

/**
 * Decodes the specified [bytes] using the charset identified by [charsetName].
 *
 * Throws if the charset does not exist or decoding fails.
 *
 * @param charsetName The canonical name or alias of the charset to use
 * @param bytes The byte sequence to decode
 * @param options Optional decoding options
 */
@JsExport
public fun decode(charsetName: String, bytes: Uint8Array, options: DecodeOptions? = null): String {
  val charset = registrar.getCharset(charsetName)
  val decoder = charset.newDecoder()

  if (options != null && options.replacement !== undefined) {
    decoder.setReplacement(options.replacement)
  }

  val byteArray = bytes.toByteArray()
  return decoder.decode(byteArray)
}

/**
 * Encodes the specified [value] using the charset identified by [charsetName].
 *
 * Throws if the charset does not exist or encoding fails.
 *
 * @param charsetName The canonical name or alias of the charset to use
 * @param value The string to encode
 * @param options Optional encoding options
 */
@JsExport
public fun encode(charsetName: String, value: String, options: EncodeOptions? = null): Uint8Array {
  val charset = registrar.getCharset(charsetName)
  val encoder = charset.newEncoder()

  if (options != null && options.replacement !== undefined) {
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

// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

@OptIn(ExperimentalStdlibApi::class)
private val decodeFormat = HexFormat {
  upperCase = true
  number {
    removeLeadingZeros = true
    minLength = 4
  }
}

@OptIn(ExperimentalStdlibApi::class)
private val encodeFormat = HexFormat {
  upperCase = true
  bytes {
    byteSeparator = ","
  }
}

internal fun String.toHexString(): String {
  val chars = this.toCharArray()
  return chars.toHexString()
}

@OptIn(ExperimentalStdlibApi::class)
internal fun CharArray.toHexString(): String =
  this.joinToString(separator = ",") {
    it.code.toHexString(decodeFormat)
  }

@OptIn(ExperimentalStdlibApi::class)
internal fun ByteArray.toHexString(): String =
  this.toHexString(encodeFormat)

internal fun XCharset.encodeToBytes(str: String, replacement: ByteArray? = byteArrayOf()): ByteArray {
  val encoder = this.newEncoder()

  if (replacement == null || replacement.isNotEmpty()) {
    encoder.setReplacement(replacement)
  }

  return encoder.encode(str)
}

internal fun XCharset.decodeToString(bytes: ByteArray, replacement: String? = ""): String {
  val decoder = this.newDecoder()

  if (replacement == null || replacement.isNotEmpty()) {
    decoder.setReplacement(replacement)
  }

  return decoder.decode(bytes)
}

@OptIn(ExperimentalStdlibApi::class)
internal fun XCharset.encodeToHexString(str: String, replacement: ByteArray? = byteArrayOf()): String {
  val bytes = this.encodeToBytes(str, replacement)
  return bytes.toHexString(encodeFormat)
}

internal fun XCharset.decodeToHexString(bytes: ByteArray, replacement: String? = ""): String {
  val str = this.decodeToString(bytes, replacement)
  return str.toHexString()
}

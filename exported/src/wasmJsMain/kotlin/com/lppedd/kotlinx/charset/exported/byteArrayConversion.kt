// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

internal actual fun Uint8Array.toByteArray(): ByteArray {
  return ByteArray(this.length) {
    this.get(it)
  }
}

internal actual fun ByteArray.toUint8Array(): Uint8Array {
  val size = this.size
  val array = Uint8Array(size)

  for (i in 0..<size) {
    array.set(i, this[i])
  }

  return array
}

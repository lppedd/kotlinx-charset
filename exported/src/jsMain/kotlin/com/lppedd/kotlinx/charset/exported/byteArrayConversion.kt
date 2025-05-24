// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array

internal fun Uint8Array.toByteArray(): ByteArray {
  val i8a = Int8Array(this.buffer, this.byteOffset, this.length)
  return i8a.unsafeCast<ByteArray>()
}

internal fun ByteArray.toUint8Array(): Uint8Array {
  val i8a = this.unsafeCast<Int8Array>()
  return Uint8Array(i8a.buffer, i8a.byteOffset, i8a.length)
}

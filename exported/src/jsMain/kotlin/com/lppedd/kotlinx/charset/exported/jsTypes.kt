// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

public actual external class Int8Array {
  public actual constructor(length: Int)
  public actual constructor(buffer: ArrayBuffer, byteOffset: Int, length: Int)

  public actual val length: Int
  public actual val buffer: ArrayBuffer
  public actual val byteOffset: Int
}

public actual external class Uint8Array {
  public actual constructor(length: Int)
  public actual constructor(buffer: ArrayBuffer, byteOffset: Int, length: Int)

  public actual val length: Int
  public actual val buffer: ArrayBuffer
  public actual val byteOffset: Int
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun Uint8Array.get(index: Int): Byte {
  return asDynamic()[index]
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun Uint8Array.set(index: Int, value: Byte) {
  asDynamic()[index] = value
}

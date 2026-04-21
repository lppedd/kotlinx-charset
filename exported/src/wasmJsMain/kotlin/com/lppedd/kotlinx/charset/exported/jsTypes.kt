// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

@OptIn(ExperimentalWasmJsInterop::class)
public actual external class Int8Array : JsAny {
  public actual constructor(length: Int)
  public actual constructor(buffer: ArrayBuffer, byteOffset: Int, length: Int)

  public actual val length: Int
  public actual val buffer: ArrayBuffer
  public actual val byteOffset: Int
}

@OptIn(ExperimentalWasmJsInterop::class)
public actual external class Uint8Array : JsAny {
  public actual constructor(length: Int)
  public actual constructor(buffer: ArrayBuffer, byteOffset: Int, length: Int)

  public actual val length: Int
  public actual val buffer: ArrayBuffer
  public actual val byteOffset: Int
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun Uint8Array.get(index: Int): Byte {
  return getImpl(this, index)
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun Uint8Array.set(index: Int, value: Byte) {
  setImpl(this, index, value)
}

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalWasmJsInterop::class)
private fun getImpl(obj: Uint8Array, index: Int): Byte {
  js("obj[index];")
}

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalWasmJsInterop::class)
private fun setImpl(obj: Uint8Array, index: Int, value: Byte) {
  js("obj[index] = value;")
}

// Copyright (c) 2026 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2026 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.exported

import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
public external class ArrayBuffer(size: Int)

public expect class Int8Array {
  public constructor(length: Int)
  public constructor(buffer: ArrayBuffer, byteOffset: Int, length: Int)

  public val length: Int
  public val buffer: ArrayBuffer
  public val byteOffset: Int
}

public expect class Uint8Array {
  public constructor(length: Int)
  public constructor(buffer: ArrayBuffer, byteOffset: Int, length: Int)

  public val length: Int
  public val buffer: ArrayBuffer
  public val byteOffset: Int
}

internal expect fun Uint8Array.get(index: Int): Byte
internal expect fun Uint8Array.set(index: Int, value: Byte)

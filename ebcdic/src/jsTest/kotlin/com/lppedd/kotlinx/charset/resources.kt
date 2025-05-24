// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

import kotlinx.io.Buffer
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString

fun readResourceText(path: String): String {
  val buffer = readToBuffer(path)
  return buffer.readString()
}

private fun readToBuffer(path: String): Buffer {
  val source = SystemFileSystem.source(Path("kotlin/$path"))
  val dest = Buffer()
  var read: Long

  do {
    read = source.readAtMostTo(dest, 4096L)
  } while (read > 0)

  return dest
}

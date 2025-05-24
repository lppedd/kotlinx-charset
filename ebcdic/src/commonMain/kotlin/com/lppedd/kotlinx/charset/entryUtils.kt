// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

internal fun Array<out Entry>.binarySearch(key: Entry, comp: Comparator<Entry>): Int {
  var low = 0
  var high = this.size - 1

  while (low <= high) {
    val mid = (low + high) ushr 1
    val midVal = this[mid]
    val cmp = comp.compare(midVal, key)

    if (cmp < 0) {
      low = mid + 1
    } else if (cmp > 0) {
      high = mid - 1
    } else {
      return mid
    }
  }

  return -(low + 1)
}

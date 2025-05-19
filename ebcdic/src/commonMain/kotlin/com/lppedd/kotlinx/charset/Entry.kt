package com.lppedd.kotlinx.charset

import kotlin.jvm.JvmField

/**
 * Represents a mapping to/from a composite character sequence, e.g. `0xECC2 U+31F7+309A`.
 *
 * @author Edoardo Luppi
 */
internal class Entry(
  @JvmField var bs: Int = 0,
  @JvmField var cp: Char = 0.toChar(),
  @JvmField var cp2: Char = 0.toChar(),
)

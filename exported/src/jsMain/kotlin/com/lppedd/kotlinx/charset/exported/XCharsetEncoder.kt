package com.lppedd.kotlinx.charset.exported

import org.khronos.webgl.Uint8Array

/**
 * @author Edoardo Luppi
 */
@JsExport
public interface XCharsetEncoder {
  public fun encode(value: String): Uint8Array
  public fun setReplacement(newReplacement: Uint8Array?)
  public fun reset()
}

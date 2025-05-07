package com.github.lppedd.kotlinx.charset.exported

import org.khronos.webgl.Uint8Array

/**
 * @author Edoardo Luppi
 */
@JsExport
public interface XCharsetDecoder {
  public fun decode(bytes: Uint8Array): String
  public fun setReplacement(newReplacement: String?)
  public fun reset()
}

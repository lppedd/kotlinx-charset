package com.github.lppedd.kotlinx.charset.exported

import org.khronos.webgl.Uint8Array
import com.github.lppedd.kotlinx.charset.XCharsetDecoder as CoreCharsetDecoder

/**
 * @author Edoardo Luppi
 */
internal class DelegatingCharsetDecoder(private val delegate: CoreCharsetDecoder) : XCharsetDecoder {
  override fun decode(bytes: Uint8Array): String =
    delegate.decode(bytes.toByteArray())

  override fun setReplacement(newReplacement: String?) {
    delegate.setReplacement(newReplacement)
  }

  override fun reset() {
    delegate.reset()
  }

  @JsExport.Ignore
  override fun toString(): String =
    "(Exported) $delegate"
}

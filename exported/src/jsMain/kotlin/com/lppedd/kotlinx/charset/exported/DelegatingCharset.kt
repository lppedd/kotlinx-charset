package com.lppedd.kotlinx.charset.exported

import com.lppedd.kotlinx.charset.XCharset as CoreCharset

/**
 * @author Edoardo Luppi
 */
internal class DelegatingCharset(private val delegate: CoreCharset) : XCharset {
  override val name: String
    get() = delegate.name

  override val aliases: Array<String>
    get() = delegate.aliases

  override fun newDecoder(): XCharsetDecoder =
    DelegatingCharsetDecoder(delegate.newDecoder())

  override fun newEncoder(): XCharsetEncoder =
    DelegatingCharsetEncoder(delegate.newEncoder())
}

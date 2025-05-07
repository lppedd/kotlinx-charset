package com.github.lppedd.kotlinx.charset.exported

/**
 * @author Edoardo Luppi
 */
@JsExport
public interface XCharset {
  public val name: String
  public val aliases: Array<String>
  public fun newDecoder(): XCharsetDecoder
  public fun newEncoder(): XCharsetEncoder
}

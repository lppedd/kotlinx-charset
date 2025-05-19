<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="className" type="java.lang.String" -->
package ${packageName}

internal expect object ${className} : com.lppedd.kotlinx.charset.XCharset {
  override val name: String
  override val aliases: Array<String>

  override fun newDecoder(): com.lppedd.kotlinx.charset.XCharsetDecoder
  override fun newEncoder(): com.lppedd.kotlinx.charset.XCharsetEncoder
}

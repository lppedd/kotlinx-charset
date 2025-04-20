<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="className" type="java.lang.String" -->
package ${packageName}

internal expect object ${className} : com.github.lppedd.kotlinx.charset.XCharset {
  override val name: String
  override val aliases: Array<String>

  override fun newDecoder(): com.github.lppedd.kotlinx.charset.XCharsetDecoder
  override fun newEncoder(): com.github.lppedd.kotlinx.charset.XCharsetEncoder
}

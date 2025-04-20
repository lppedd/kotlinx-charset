<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="className" type="java.lang.String" -->
<#-- @ftlvariable name="charsetName" type="java.lang.String" -->
package ${packageName}

internal actual object ${className}
  : com.github.lppedd.kotlinx.charset.JvmCharset(charset("${charsetName}")),
    com.github.lppedd.kotlinx.charset.XCharset

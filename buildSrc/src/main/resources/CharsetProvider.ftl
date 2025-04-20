<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="classNames" type="java.util.List<java.lang.String>" -->
@file:JvmName("CharsetProvider")

package ${packageName}

import kotlin.jvm.JvmName

public fun provideCharsets(registrar: com.github.lppedd.kotlinx.charset.XCharsetRegistrar) {
  <#list classNames as className>
  registrar.registerCharset(${className})
  </#list>
}

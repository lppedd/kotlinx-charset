<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="className" type="java.lang.String" -->
<#-- @ftlvariable name="isCommon" type="java.lang.Boolean" -->
<#-- @ftlvariable name="charset" type="com.lppedd.kotlinx.charset.GenerateCharsetTask.SbcsCharset" -->
package ${packageName}

internal <#if isCommon == false>actual </#if>object ${className} : com.lppedd.kotlinx.charset.XCharset {
  private val b2c: CharArray
  private val c2b: CharArray
  private val c2bIndex: CharArray

  <#if isCommon == false>actual </#if>override val name: String =
    "${charset.name}"

  <#if isCommon == false>actual </#if>override val aliases: Array<String>
     get() = arrayOf(<#list charset.aliases as alias>"${alias}"<#sep>, </#list>)

  <#if isCommon == false>actual </#if>override fun newDecoder(): com.lppedd.kotlinx.charset.XCharsetDecoder =
    com.lppedd.kotlinx.charset.SbcsDecoder(b2c)

  <#if isCommon == false>actual </#if>override fun newEncoder(): com.lppedd.kotlinx.charset.XCharsetEncoder =
    com.lppedd.kotlinx.charset.SbcsEncoder(c2b, c2bIndex)

  override fun toString(): String =
    name

  init {
    val b2cStr = "${charset.b2cStr}"
    b2c = b2cStr.toCharArray()
    c2b = CharArray(${charset.c2bSize})
    c2bIndex = CharArray(256)

    val b2cMap = b2cStr.toCharArray()
    <#list charset.b2cNR>
    <#--
    Override non-roundtrip characters in b2cMap.
    These are characters that *must not* map to specific bytes.

    For example, in IBM037 the character \u000A *must not* map to byte 0x25,
    and should instead map to another byte (0x15) or to UNMAPPABLE_ENCODING.
    -->

    // Remove non-roundtrip .nr entries
    <#items as entry>
    b2cMap[${toHex(entry.bs, 2)}] = com.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_DECODING
    </#items>
    </#list>

    // Non-roundtrip .c2b entries
    val c2bNR = CharArray(${charset.c2bNR?size * 2})
    <#list charset.c2bNR>
    <#items as entry>
    <#assign i = entry?index * 2>
    c2bNR[${i}] = ${toHex(entry.bs, 2)}.toChar(); c2bNR[${i + 1}] = '${toUnicode(entry.cp)}'
    </#items>
    </#list>

    com.lppedd.kotlinx.charset.Sbcs.initC2B(b2cMap, c2bNR, c2b, c2bIndex)
  }
}

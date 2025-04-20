<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="className" type="java.lang.String" -->
<#-- @ftlvariable name="charset" type="com.github.lppedd.kotlinx.charset.GenerateCharsetTask.SbcsCharset" -->
package ${packageName}

internal actual object ${className} : com.github.lppedd.kotlinx.charset.XCharset {
  private val b2c: CharArray
  private val c2b: CharArray
  private val c2bIndex: CharArray

  actual override val name: String =
    "${charset.name}"

  actual override val aliases: Array<String>
     get() = arrayOf(<#list charset.aliases as alias>"${alias}"<#sep>, </#list>)

  actual override fun newDecoder(): com.github.lppedd.kotlinx.charset.XCharsetDecoder =
    com.github.lppedd.kotlinx.charset.SbcsDecoder(b2c)

  actual override fun newEncoder(): com.github.lppedd.kotlinx.charset.XCharsetEncoder =
    com.github.lppedd.kotlinx.charset.SbcsEncoder(c2b, c2bIndex)

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
    b2cMap[${toHex(entry.bs, 2)}] = com.github.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_DECODING
    </#items>
    </#list>

    // Non-roundtrip .c2b entries
    val c2bNR = CharArray(${charset.c2bNR?size * 2})
    <#list charset.c2bNR>
    <#items as entry>
    c2bNR[${entry?index}] = '${toUnicode(entry.cp)}'; c2bNR[${entry?index + 1}] = ${toHex(entry.bs, 2)}.toChar()
    </#items>
    </#list>

    com.github.lppedd.kotlinx.charset.Sbcs.initC2B(b2cMap, c2bNR, c2b, c2bIndex)
  }
}

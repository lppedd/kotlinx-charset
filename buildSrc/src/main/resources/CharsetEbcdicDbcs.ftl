<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="className" type="java.lang.String" -->
<#-- @ftlvariable name="isCommon" type="java.lang.Boolean" -->
<#-- @ftlvariable name="charset" type="com.lppedd.kotlinx.charset.GenerateCharsetTask.EbcdicDbcsCharset" -->
package ${packageName}

@Suppress("ConstPropertyName")
internal <#if isCommon == false>actual </#if>object ${className} : com.lppedd.kotlinx.charset.XCharset {
  private const val b2Min: Int = ${toHex(charset.b2Min, 4)}
  private const val b2Max: Int = ${toHex(charset.b2Max, 4)}

  private val b2cSB: CharArray
  private val b2c: Array<CharArray>
  private val c2b: CharArray
  private val c2bIndex: CharArray

  <#if isCommon == false>actual </#if>override val name: String =
    "${charset.name}"

  <#if isCommon == false>actual </#if>override val aliases: Array<String>
     get() = arrayOf(<#list charset.aliases as alias>"${alias}"<#sep>, </#list>)

  <#if isCommon == false>actual </#if>override fun newDecoder(): com.lppedd.kotlinx.charset.XCharsetDecoder =
    com.lppedd.kotlinx.charset.EbcdicDbcsDecoder(b2Min, b2Max, b2cSB, b2c)

  <#if isCommon == false>actual </#if>override fun newEncoder(): com.lppedd.kotlinx.charset.XCharsetEncoder =
    com.lppedd.kotlinx.charset.EbcdicDbcsEncoder(c2b, c2bIndex)

  override fun toString(): String =
    name

  init {
    val b2cSBStr = "${charset.b2cSBStr}"

    @Suppress("RemoveExplicitTypeArguments")
    val b2cStr = arrayOf<String?>(
      <#list charset.b2cStrEntries as entry>
      ${entry},
      </#list>
    )

    b2cSB = b2cSBStr.toCharArray()

    val b2cUnmappable = CharArray(256) { com.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_DECODING }
    b2c = Array(b2cStr.size) {
      val str = b2cStr[it]

      @Suppress("IfThenToElvis")
      if (str == null) {
        b2cUnmappable
      } else {
        str.toCharArray()
      }
    }

    c2b = CharArray(${charset.c2bSize})
    c2bIndex = CharArray(256)

    // Non-roundtrip .nr entries
    val b2cNR = "${charset.b2cNR}"

    // Non-roundtrip .c2b entries
    val c2bNR = "${charset.c2bNR}"

    com.lppedd.kotlinx.charset.Dbcs.initC2B(b2cStr, b2cSBStr, b2cNR, c2bNR, b2Min, b2Max, c2b, c2bIndex)
  }
}

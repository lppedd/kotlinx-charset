<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="className" type="java.lang.String" -->
<#-- @ftlvariable name="charset" type="com.github.lppedd.kotlinx.charset.GenerateCharsetTask.EbcdicDbcsCharset" -->
package ${packageName}

@Suppress("ConstPropertyName")
internal actual object ${className} : com.github.lppedd.kotlinx.charset.XCharset {
  private const val b2Min: Int = ${toHex(charset.b2Min, 4)}
  private const val b2Max: Int = ${toHex(charset.b2Max, 4)}

  private val b2cSB: CharArray
  private val b2c: Array<CharArray>
  private val c2b: CharArray
  private val c2bIndex: CharArray

  actual override val name: String =
    "${charset.name}"

  actual override val aliases: Array<String>
     get() = arrayOf(<#list charset.aliases as alias>"${alias}"<#sep>, </#list>)

  actual override fun newDecoder(): com.github.lppedd.kotlinx.charset.XCharsetDecoder =
    com.github.lppedd.kotlinx.charset.EbcdicDbcsDecoder(b2Min, b2Max, b2cSB, b2c)

  actual override fun newEncoder(): com.github.lppedd.kotlinx.charset.XCharsetEncoder =
    com.github.lppedd.kotlinx.charset.EbcdicDbcsEncoder(c2b, c2bIndex)

  init {
    val b2cSBStr = "${charset.b2cSBStr}"

    @Suppress("RemoveExplicitTypeArguments")
    val b2cStr = arrayOf<String?>(
      <#list charset.b2cStrEntries as entry>
      ${entry},
      </#list>
    )

    b2cSB = b2cSBStr.toCharArray()

    val b2cUnmappable = CharArray(256) { com.github.lppedd.kotlinx.charset.CharsetMapping.UNMAPPABLE_DECODING }
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

    com.github.lppedd.kotlinx.charset.Dbcs.initC2B(b2cStr, b2cSBStr, b2cNR, c2bNR, b2Min, b2Max, c2b, c2bIndex)
  }
}

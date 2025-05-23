<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="className" type="java.lang.String" -->
<#-- @ftlvariable name="isCommon" type="java.lang.Boolean" -->
<#-- @ftlvariable name="charset" type="com.lppedd.kotlinx.charset.GenerateCharsetTask.ExtendedEbcdicDbcsCharset" -->
package ${packageName}

@Suppress("ConstPropertyName", "JoinDeclarationAndAssignment")
internal <#if isCommon == false>actual </#if>object ${className} : com.lppedd.kotlinx.charset.XCharset {
  private const val b2Min: Int = ${toHex(charset.b2Min, 4)}
  private const val b2Max: Int = ${toHex(charset.b2Max, 4)}

  private val b2cSB: CharArray
  private val b2c: Array<IntArray>
  private val c2b: CharArray
  private val c2bIndex: IntArray
  private val b2cComposites: Array<out com.lppedd.kotlinx.charset.Entry>
  private val c2bComposites: Array<out com.lppedd.kotlinx.charset.Entry>

  <#if isCommon == false>actual </#if>override val name: String =
    "${charset.name}"

  <#if isCommon == false>actual </#if>override val aliases: Array<String>
     get() = arrayOf(<#list charset.aliases as alias>"${alias}"<#sep>, </#list>)

  <#if isCommon == false>actual </#if>override fun newDecoder(): com.lppedd.kotlinx.charset.XCharsetDecoder =
    com.lppedd.kotlinx.charset.ExtendedEbcdicDbcsDecoder(b2Min, b2Max, b2cSB, b2c, b2cComposites)

  <#if isCommon == false>actual </#if>override fun newEncoder(): com.lppedd.kotlinx.charset.XCharsetEncoder =
    com.lppedd.kotlinx.charset.ExtendedEbcdicDbcsEncoder(c2b, c2bIndex, c2bComposites)

  override fun toString(): String =
    name

  <#assign chunkSize = 40>
  <#list charset.b2c?chunk(chunkSize) as chunk>
  private fun initB2C${chunk?index}(b2c: Array<IntArray?>) {
    <#list chunk as row>
    b2c[${row.index}] = intArrayOf(<#list row.cps as cp>${toHex(cp, 6)}<#sep>, </#sep></#list>)
    </#list>
  }
  <#sep>

  </#sep>
  </#list>

  init {
    // b2c entries are sorted by byte sequence to work with binary search
    b2cComposites = arrayOf(
      <#list charset.b2cComposites as entry>
      com.lppedd.kotlinx.charset.Entry(${toHex(entry.bs, -1)}, '${toUnicode(entry.cp)}', '${toUnicode(entry.cp2)}'),
      </#list>
    )

    // c2b entries must be sorted by cp and cp2 to work with binary search
    c2bComposites = b2cComposites.sortedArrayWith(compareBy({ it.cp }, { it.cp2 }))

    val b2cSBStr = "<#list charset.b2cSB as cp>${toUnicode(cp)}</#list>"
    b2cSB = b2cSBStr.toCharArray()

    // We have to split up the initialization to avoid MethodTooLargeException errors under the JVM
    val b2cTemp = Array<IntArray?>(256) { null }
    <#list charset.b2c?chunk(chunkSize) as chunk>
    initB2C${chunk?index}(b2cTemp)
    </#list>

    // Reuse the same array for unmapped segments to save memory
    val b2cUnmapped = IntArray(256) { 0xFFFD /* unmappable decoding */ }
    b2c = Array(b2cTemp.size) {
      val ints = b2cTemp[it]
      ints?.copyOf() ?: b2cUnmapped
    }

    c2b = CharArray(${charset.c2bSize})
    c2bIndex = IntArray(0x110000 shr 8)

    // Non-roundtrip .nr entries
    val b2cNR = intArrayOf(
      <#list charset.b2cNR as v>${toHex(v, -1)}, <#if v?index != 0 && v?index < (charset.b2cNR?size - 1) && (v?index + 1) % 2 == 0>${"\n      "}</#if></#list>
    )

    // Non-roundtrip .c2b entries
    val c2bNR = intArrayOf(
      <#list charset.c2bNR as v>${toHex(v, -1)}, <#if v?index != 0 && v?index < (charset.c2bNR?size - 1) && (v?index + 1) % 2 == 0>${"\n      "}</#if></#list>
    )

    com.lppedd.kotlinx.charset.ExtendedDbcs.initC2B(b2cTemp, b2cSB.copyOf(), b2cNR, c2bNR, b2Min, b2Max, c2b, c2bIndex)
  }
}

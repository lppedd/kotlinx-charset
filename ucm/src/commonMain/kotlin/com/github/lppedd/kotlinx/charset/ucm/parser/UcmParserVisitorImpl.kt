package com.github.lppedd.kotlinx.charset.ucm.parser

import com.github.lppedd.kotlinx.charset.ucm.UcmCodeSetType
import com.github.lppedd.kotlinx.charset.ucm.UcmMapping
import org.antlr.v4.kotlinruntime.tree.TerminalNode

/**
 * @author Edoardo Luppi
 */
internal class UcmParserVisitorImpl : UcmParserBaseVisitor<VisitorUcmData>() {
  private val data = VisitorUcmData()

  override fun defaultResult(): VisitorUcmData =
    data

  override fun visitAttribute(ctx: UcmParser.AttributeContext): VisitorUcmData {
    val attrName = ctx.AttrName().text
    var attrValue = ctx.AttrValue().text

    if (attrValue.startsWith("\"")) {
      // Remove the starting and ending double quotes
      attrValue = attrValue.substring(1, attrValue.length - 1)
    }

    when (attrName) {
      "code_set_name" -> data.codeSetName = attrValue
      "mb_cur_max" -> data.maxBpc = attrValue.toInt()
      "mb_cur_min" -> data.minBpc = attrValue.toInt()
      "uconv_class" -> data.codeSetType = UcmCodeSetType.valueOf(attrValue)
      "subchar" -> data.subchar = parseByteSequence(attrValue)
      "subchar1" -> data.subchar1 = parseByteSequence(attrValue).first()
      "char_name_mask",
      "icu:charsetFamily",
      "icu:alias",
      "icu:state" -> {
        // We will consider them in the future
      }
      else -> throw UnsupportedOperationException("Unsupported ucm attribute: $attrName")
    }

    return data
  }

  override fun visitMapping(ctx: UcmParser.MappingContext): VisitorUcmData {
    val codePoints = parseCodepointNodes(ctx.Codepoint())
    val bytes = parseByteNodes(ctx.Byte())
    val precision = parseTypeNode(ctx.Type())

    if (precision !in 0..4) {
      throw UcmParseException("Invalid mapping precision value: $precision")
    }

    data.mappings += UcmMapping(
      bs = bytes,
      cs = codePoints,
      precision = precision,
    )

    return data
  }

  private fun parseCodepointNodes(nodes: List<TerminalNode>): IntArray =
    IntArray(nodes.size) {
      // Get rid of the initial U char
      val text = nodes[it].text.substring(1)
      text.toInt(radix = 16)
    }

  private fun parseByteNodes(byteNodes: List<TerminalNode>): Int {
    val str = byteNodes.joinToString(separator = "") {
      // Get rid of the initial \x chars
      it.text.substring(2)
    }

    return str.toInt(radix = 16)
  }

  private fun parseTypeNode(type: TerminalNode): Int {
    // Get rid of the initial | char
    val str = type.text.substring(1)
    return str.toInt()
  }

  private fun parseByteSequence(attrValue: String): CharArray {
    // Drop the initial \x, and then split by that
    // so we automatically get rid of them
    val bytesStr = attrValue.substring(2).split("\\x")
    return bytesStr
      .map { it.toInt(radix = 16).toChar() }
      .toCharArray()
  }
}

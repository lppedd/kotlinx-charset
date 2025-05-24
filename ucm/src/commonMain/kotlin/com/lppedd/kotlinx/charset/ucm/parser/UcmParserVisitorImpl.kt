// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset.ucm.parser

import com.lppedd.kotlinx.charset.ucm.UcmCodeSetType
import com.lppedd.kotlinx.charset.ucm.UcmData
import com.lppedd.kotlinx.charset.ucm.UcmMapping
import org.antlr.v4.kotlinruntime.tree.TerminalNode

/**
 * @author Edoardo Luppi
 */
internal class UcmParserVisitorImpl : UcmParserBaseVisitor<Any>() {
  override fun defaultResult(): Nothing =
    error("Should not be called")

  override fun visitUcm(ctx: UcmParser.UcmContext): UcmData {
    var codeSetName = ""
    var codeSetType = UcmCodeSetType.SBCS
    var minBpc = 0
    var maxBpc = 0
    var subchar: CharArray? = null
    var subchar1: Char? = null

    for (attributeContext in ctx.attribute()) {
      val (name, value) = visitAttribute(attributeContext)

      when (name) {
        "code_set_name" -> codeSetName = value.trim()
        "mb_cur_max" -> maxBpc = value.toInt()
        "mb_cur_min" -> minBpc = value.toInt()
        "uconv_class" -> codeSetType = UcmCodeSetType.valueOf(value)
        "subchar" -> subchar = parseByteSequence(value)
        "subchar1" -> subchar1 = parseByteSequence(value).first()
        "char_name_mask",
        "icu:charsetFamily",
        "icu:alias",
        "icu:state" -> {
          // We will consider them in the future
        }
        else -> throw UnsupportedOperationException("Unsupported ucm attribute '$name'")
      }
    }

    checkUcm(codeSetName.isNotEmpty(), "The charset name must not be empty")
    checkUcm(minBpc > 0, "The min number of bytes per character must be at least 1, but is $minBpc")
    checkUcm(maxBpc > 0, "The max number of bytes per character must be at least 1, but is $maxBpc")

    val mappings = ctx.mapping().mapTo(ArrayList(), ::visitMapping)
    checkUcm(mappings.isNotEmpty(), "There must be at least one Unicode to byte mapping")
    mappings.sortBy(UcmMapping::bs)

    return UcmData(
      codeSetName = codeSetName,
      codeSetType = codeSetType,
      minBpc = minBpc,
      maxBpc = maxBpc,
      subchar = subchar,
      subchar1 = subchar1,
      mappings = mappings,
    )
  }

  override fun visitAttribute(ctx: UcmParser.AttributeContext): Pair<String, String> {
    val name = ctx.AttrName().text
    var value = ctx.AttrValue().text

    if (value.startsWith("\"")) {
      // Remove the starting and ending double quotes
      value = value.substring(1, value.length - 1)
    }

    return Pair(name, value)
  }

  override fun visitMapping(ctx: UcmParser.MappingContext): UcmMapping {
    val codePoints = parseCodepointNodes(ctx.Codepoint())
    val bytes = parseByteNodes(ctx.Byte())
    val precision = parseTypeNode(ctx.Type())
    checkUcm(precision in 0..4, "Invalid mapping precision value '$precision'")

    return UcmMapping(
      bs = bytes,
      cs = codePoints,
      precision = precision,
    )
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

  private fun checkUcm(value: Boolean, message: String) {
    if (!value) {
      throw UcmParseException(message)
    }
  }
}

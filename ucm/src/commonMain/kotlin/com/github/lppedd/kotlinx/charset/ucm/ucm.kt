@file:JvmName("Ucm")

package com.github.lppedd.kotlinx.charset.ucm

import com.github.lppedd.kotlinx.charset.ucm.parser.FailFastErrorListener
import com.github.lppedd.kotlinx.charset.ucm.parser.UcmLexer
import com.github.lppedd.kotlinx.charset.ucm.parser.UcmParser
import com.github.lppedd.kotlinx.charset.ucm.parser.UcmParserVisitorImpl
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import kotlin.jvm.JvmName

/**
 * Parses the content of a `ucm` file.
 *
 * See the [ucm file format](https://unicode-org.github.io/icu/userguide/conversion/data.html#ucm-file-format).
 */
public fun parseUcmContent(ucmContent: String): UcmData {
  val charStream = CharStreams.fromString(ucmContent)
  val lexer = UcmLexer(charStream).apply {
    // Ensure the lexer fails immediately
    removeErrorListeners()
    addErrorListener(FailFastErrorListener())
  }

  val tokenStream = CommonTokenStream(lexer)
  val parser = UcmParser(tokenStream).apply {
    // Ensure the parser fails immediately
    removeErrorListeners()
    addErrorListener(FailFastErrorListener())
  }

  val visitor = UcmParserVisitorImpl()
  return visitor.visitUcm(parser.ucm())
}

/**
 * Converts parsed `ucm` data to `map`, `nr`, and `c2b` entries.
 */
public fun convertUcmToMap(ucmData: UcmData): MapData {
  val mapEntries = ArrayList<UcmMapping>()
  val nrEntries = ArrayList<UcmMapping>()
  val c2bEntries = ArrayList<UcmMapping>()

  for (entry in ucmData.mappings) {
    when (entry.precision) {
      // A roundtrip mapping from a Unicode code point and back
      0 -> mapEntries.add(entry)
      // A "fallback" mapping only from Unicode to the codepage, but not back
      1 -> c2bEntries.add(entry)
      // The Unicode code point is unmappable. Use "subchar1" as target replacement.
      2 -> {
        // TODO: need to understand how to correctly interpret a "subchar1 mapping"
        // c2bEntries.add(UcmMapping(0x3F, entry.cs, entry.precision))
      }
      // A "reverse fallback" mapping only from the codepage to Unicode, but not back to the codepage
      3 -> {
        mapEntries.add(entry)
        nrEntries.add(entry)
      }
      4 -> throw UnsupportedOperationException("Unsupported good one-way mapping")
    }
  }

  return MapData(
    mapEntries = mapEntries,
    nrEntries = nrEntries,
    c2bEntries = c2bEntries,
  )
}

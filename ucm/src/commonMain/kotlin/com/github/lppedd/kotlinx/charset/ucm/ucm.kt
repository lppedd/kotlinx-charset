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
public fun parseUcmContent(content: String): UcmData {
  val charStream = CharStreams.fromString(content)
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

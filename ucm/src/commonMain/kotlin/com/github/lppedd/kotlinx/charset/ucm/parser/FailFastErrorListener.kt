package com.github.lppedd.kotlinx.charset.ucm.parser

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer

/**
 * @author Edoardo Luppi
 */
internal class FailFastErrorListener : BaseErrorListener() {
  override fun syntaxError(
    recognizer: Recognizer<*, *>,
    offendingSymbol: Any?,
    line: Int,
    charPositionInLine: Int,
    msg: String,
    e: RecognitionException?,
  ) {
    val type = if (recognizer is Parser) "Parser" else "Lexer"
    throw UcmParseException("$type token recognition error at line $line, column $charPositionInLine: $msg", e)
  }
}

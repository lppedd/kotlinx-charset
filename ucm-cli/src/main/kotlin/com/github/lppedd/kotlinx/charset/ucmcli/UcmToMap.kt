package com.github.lppedd.kotlinx.charset.ucmcli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.types.path
import com.github.lppedd.kotlinx.charset.ucm.UcmMapping
import com.github.lppedd.kotlinx.charset.ucm.convertUcmToMap
import com.github.lppedd.kotlinx.charset.ucm.parseUcmContent
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * @author Edoardo Luppi
 */
class UcmToMap : CliktCommand(name = "to-map") {
  private val filePath by argument()
    .path(mustExist = true, mustBeReadable = true, canBeDir = false)

  private val destName by argument()
  private val destPath by argument()
    .path(mustBeWritable = true)
    .default(Path("")) // Defaults to cwd

  override fun run() {
    val ucmContent = filePath.readText().trim()
    val ucmData = parseUcmContent(ucmContent)
    val mapData = convertUcmToMap(ucmData)

    val ls = System.lineSeparator()
    val mapPath = destPath.resolve("$destName.map")
    val mapText = mapData.mapEntries.joinToString(ls, postfix = ls, transform = ::entryToString)
    mapPath.writeText(mapText)

    val nrPath = destPath.resolve("$destName.nr")
    val nrText = mapData.nrEntries.joinToString(separator = ls, postfix = ls, transform = ::entryToString)
    nrPath.writeText(nrText)

    val c2bPath = destPath.resolve("$destName.c2b")
    val c2bText = mapData.c2bEntries.joinToString(separator = ls, postfix = ls, transform = ::entryToString)
    c2bPath.writeText(c2bText)
  }

  @OptIn(ExperimentalStdlibApi::class)
  private fun entryToString(entry: UcmMapping): String {
    val bsStr = "0x" + entry.bs.toHexString(bsPrintFormat)
    val csStr = entry.cs.joinToString(separator = "+", prefix = "U+") {
      it.toHexString(cpPrintFormat)
    }

    return "${bsStr.padEnd(6)}  $csStr"
  }
}

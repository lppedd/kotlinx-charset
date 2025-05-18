@file:JvmName("Main")

package com.github.lppedd.kotlinx.charset.ucmcli

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
  UcmCli()
    .subcommands(UcmToMap())
    .main(args)
}

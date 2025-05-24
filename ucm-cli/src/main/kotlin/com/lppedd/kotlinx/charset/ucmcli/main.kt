// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
@file:JvmName("Main")

package com.lppedd.kotlinx.charset.ucmcli

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
  UcmCli()
    .subcommands(UcmToMap())
    .main(args)
}

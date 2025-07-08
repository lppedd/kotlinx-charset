// Copyright (c) 2025 Edoardo Luppi, licensed under the MIT License
// SPDX-FileCopyrightText: 2025 Edoardo Luppi
// SPDX-License-Identifier: MIT
package com.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
public class XCharsetRegistrar {
  private val map = HashMap<String, XCharset>(16)

  /**
   * Returns whether the registrar contains the named charset.
   *
   * @param charsetName The canonical name of the charset, or an alias
   */
  public fun hasCharset(charsetName: String): Boolean {
    val charset = map[charsetName.lowercase()]
    return charset != null
  }

  /**
   * Returns a charset instance for the named charset.
   *
   * @param charsetName The canonical name of the charset, or an alias
   */
  public fun getCharset(charsetName: String): XCharset {
    val charset = map[charsetName.lowercase()]

    if (charset != null) {
      return charset
    }

    throw IllegalArgumentException("Charset $charsetName could not be found")
  }

  /**
   * Returns all registered charset instances.
   */
  public fun getCharsets(): List<XCharset> =
    map.values.distinct()

  /**
   * Registers a new charset implementation.
   *
   * The charset will be discoverable via its canonical name or via its aliases
   * using the [getCharset] function.
   */
  public fun registerCharset(charset: XCharset) {
    registerCharset(map, charset)
  }

  private fun registerCharset(map: MutableMap<String, XCharset>, charset: XCharset) {
    val keys = HashSet<String>()
    keys.add(charset.name.lowercase())
    keys.addAll(charset.aliases.map(String::lowercase))

    for (key in keys) {
      checkAvailability(map, key)
      map[key] = charset
    }
  }

  /**
   * Checks whether a charset is already mapped to [key].
   */
  private fun checkAvailability(map: Map<String, XCharset>, key: String) {
    if (map.containsKey(key)) {
      throw IllegalArgumentException("A charset with name or alias '$key' is already registered")
    }
  }
}

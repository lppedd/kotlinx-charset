package com.github.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
public class XCharsetRegistrar {
  private val map = HashMap<String, XCharset>(16)

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

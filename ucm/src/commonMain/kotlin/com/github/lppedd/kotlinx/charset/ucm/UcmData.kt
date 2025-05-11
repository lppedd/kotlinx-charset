package com.github.lppedd.kotlinx.charset.ucm

/**
 * @author Edoardo Luppi
 */
public interface UcmData {
  /** The name of the charset. */
  public val codeSetName: String

  /** The type of the charset. */
  public val codeSetType: UcmCodeSetType

  /** The minimum number of bytes per character. */
  public val minBpc: Int

  /** The maximum number of bytes per character. */
  public val maxBpc: Int

  /** The substitution character byte sequence for this codepage. */
  public val subchar: CharArray?

  /** The single byte substitution character. */
  public val subchar1: Char?

  /** The charset mapping table. */
  public val mappings: List<UcmMapping>
}

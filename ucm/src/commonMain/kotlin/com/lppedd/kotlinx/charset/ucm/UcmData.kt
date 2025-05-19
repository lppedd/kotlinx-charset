package com.lppedd.kotlinx.charset.ucm

/**
 * @property codeSetName The name of the charset
 * @property codeSetType The type of the charset
 * @property minBpc The minimum number of bytes per character
 * @property maxBpc The maximum number of bytes per character
 * @property subchar The substitution character byte sequence for this codepage
 * @property subchar1 The single byte substitution character
 * @property mappings The charset mapping table
 *
 * @author Edoardo Luppi
 */
public class UcmData(
  public val codeSetName: String,
  public val codeSetType: UcmCodeSetType,
  public val minBpc: Int,
  public val maxBpc: Int,
  public val subchar: CharArray?,
  public val subchar1: Char?,
  public val mappings: List<UcmMapping>,
)

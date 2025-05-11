package com.github.lppedd.kotlinx.charset.ucm.parser

import com.github.lppedd.kotlinx.charset.ucm.UcmCodeSetType
import com.github.lppedd.kotlinx.charset.ucm.UcmData
import com.github.lppedd.kotlinx.charset.ucm.UcmMapping

/**
 * @author Edoardo Luppi
 */
internal class VisitorUcmData(
  override var codeSetName: String = "",
  override var codeSetType: UcmCodeSetType = UcmCodeSetType.SBCS,
  override var minBpc: Int = -1,
  override var maxBpc: Int = -1,
  override var subchar: CharArray? = null,
  override var subchar1: Char? = null,
  override val mappings: MutableList<UcmMapping> = ArrayList(256),
) : UcmData

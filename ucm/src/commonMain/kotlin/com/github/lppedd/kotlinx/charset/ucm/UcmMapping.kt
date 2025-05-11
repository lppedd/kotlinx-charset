package com.github.lppedd.kotlinx.charset.ucm

/**
 * @author Edoardo Luppi
 */
@OptIn(ExperimentalStdlibApi::class)
public class UcmMapping(
  public val bs: Int,
  public val cs: IntArray,
  public val precision: Int,
) {
  override fun toString(): String {
    val bsStr = "0x" + bs.toHexString(bsPrintFormat)
    val csStr = cs.joinToString(separator = "+") {
      "U" + it.toHexString(cpPrintFormat)
    }

    return "$bsStr $csStr |$precision"
  }
}

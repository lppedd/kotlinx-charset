package com.lppedd.kotlinx.charset

// Note: an object which contains constants only is always inlined in K/JS,
//  so the Ebcdic class acts purely as namespace

/**
 * @author Edoardo Luppi
 */
internal object Ebcdic {
  /**
   * The SBCS mode identifier.
   */
  const val SBCS: Int = 0

  /**
   * The DBCS mode identifier.
   */
  const val DBCS: Int = 1

  /**
   * Shift-Out (switch to DBCS mode).
   */
  const val SO: Int = 0x0E

  /**
   * Shift-In (switch to SBCS mode).
   */
  const val SI: Int = 0x0F
}

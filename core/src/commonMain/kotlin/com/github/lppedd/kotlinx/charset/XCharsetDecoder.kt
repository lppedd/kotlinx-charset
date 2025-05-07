package com.github.lppedd.kotlinx.charset

/**
 * @author Edoardo Luppi
 */
public interface XCharsetDecoder {
  /**
   * Decodes the inputted bytes into a new string.
   *
   * This method implements an entire decoding operation, that is,
   * it resets this decoder, then it decodes the bytes to a new
   * string, and finally it flushes this decoder.
   *
   * This method should not be invoked if a decoding operation
   * is already in progress.
   */
  public fun decode(bytes: ByteArray): String

  /**
   * Sets the replacement character(s) for malformed or unmappable bytes.
   *
   * If set to `null`, a decoding error will throw a [CharacterCodingException].
   */
  public fun setReplacement(newReplacement: String?)

  /**
   * Resets the internal state of the decoder.
   *
   * It does not reset the replacement character(s).
   */
  public fun reset()
}

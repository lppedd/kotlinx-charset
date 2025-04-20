package com.github.lppedd.kotlinx.charset.exported

import com.github.lppedd.kotlinx.charset.XCharsetRegistrar
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import com.github.lppedd.kotlinx.charset.ebcdic.provideCharsets as provideEbcdicCharsets

private val registrar = initCharsetRegistrar()

@JsExport
public fun decode(charsetName: String, bytes: Uint8Array): String {
  val charset = registrar.getCharset(charsetName)
  val decoder = charset.newDecoder()
  val byteArray = bytes.toByteArray()
  return decoder.decode(byteArray)
}

@JsExport
public fun encode(charsetName: String, value: String): Uint8Array {
  val charset = registrar.getCharset(charsetName)
  val encoder = charset.newEncoder()
  val byteArray = encoder.encode(value)
  return byteArray.toUint8Array()
}

private fun initCharsetRegistrar(): XCharsetRegistrar {
  val registrar = XCharsetRegistrar()
  provideEbcdicCharsets(registrar)
  return registrar
}

private fun Uint8Array.toByteArray(): ByteArray {
  val i8a = Int8Array(this.buffer, this.byteOffset, this.length)
  return i8a.unsafeCast<ByteArray>()
}

public fun ByteArray.toUint8Array(): Uint8Array {
  val i8a = this.unsafeCast<Int8Array>()
  return Uint8Array(i8a.buffer, i8a.byteOffset, i8a.length)
}

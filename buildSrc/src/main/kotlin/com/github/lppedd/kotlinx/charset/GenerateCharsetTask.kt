package com.github.lppedd.kotlinx.charset

import freemarker.template.Configuration
import freemarker.template.SimpleNumber
import freemarker.template.TemplateMethodModelEx
import freemarker.template.Version
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.NonNullApi
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.container
import org.gradle.work.NormalizeLineEndings
import java.io.File
import java.util.*

/**
 * @author Edoardo Luppi
 */
@NonNullApi
@CacheableTask
abstract class GenerateCharsetTask : DefaultTask() {
  private companion object {
    private const val UNMAPPABLE_DECODING = "\\uFFFD"

    private val wsRegex = Regex("\\s+")
    private val freemarker = Configuration(Version("2.3.32")).also {
      it.setClassForTemplateLoading(GenerateCharsetTask::class.java, "/")
      it.defaultEncoding = "UTF-8"
      it.numberFormat = "c"
    }
  }

  /**
   * @property bs The hex byte sequence parsed as an int.
   *   The byte sequence may represent 1 byte in case of SBCS,
   *   or 2 bytes in case of DBCS.
   * @property cp The hex codepoint value parsed as an int
   */
  internal data class MappingEntry(val bs: Int, val cp: Int) : Comparable<MappingEntry> {
    override fun compareTo(other: MappingEntry): Int =
      bs - other.bs

    override fun equals(other: Any?): Boolean =
      other is MappingEntry && other.bs == bs

    override fun hashCode(): Int =
      bs

    override fun toString(): String =
      bs.toHex() + " --> " + cp.toUnicodeChar()
  }

  /**
   * @property bs The hex byte sequence parsed as an int.
   *   The byte sequence may represent 1 byte in case of SBCS,
   *   or 2 bytes in case of DBCS.
   * @property cs The hex codepoint value parsed as an int
   */
  internal data class MappingEntry2(val bs: Int, val cs: IntArray) : Comparable<MappingEntry2> {
    override fun compareTo(other: MappingEntry2): Int =
      bs - other.bs

    override fun equals(other: Any?): Boolean =
      other is MappingEntry2 && other.bs == bs

    override fun hashCode(): Int =
      bs

    override fun toString(): String =
      bs.toHex() + " --> " + cs.toUnicodeChars()
  }

  internal object HexFunction : TemplateMethodModelEx {
    override fun exec(arguments: List<*>): Any {
      val value = (arguments[0] as SimpleNumber).asNumber.toInt()
      val length = (arguments[1] as SimpleNumber).asNumber.toInt()
      return if (length < 0) {
        "0x${Integer.toHexString(value).uppercase()}"
      } else {
        String.format("0x%0${length}X", value)
      }
    }
  }

  internal object UnicodeFunction : TemplateMethodModelEx {
    override fun exec(arguments: List<*>): Any {
      val value = arguments[0] as SimpleNumber
      return String.format("\\u%04X", value.asNumber.toInt())
    }
  }

  internal data class SbcsCharset(
    val name: String,
    val aliases: List<String>,
    val b2cStr: String,
    val c2bSize: Int,
    val b2cNR: List<MappingEntry>,
    val c2bNR: List<MappingEntry>,
  )

  internal data class EbcdicDbcsCharset(
    val name: String,
    val aliases: List<String>,
    val b2Min: Int,
    val b2Max: Int,
    val b2cSBStr: String,
    val b2cStrEntries: List<String>,
    val c2bSize: Int,
    val b2cNR: String,
    val c2bNR: String,
  )

  internal class ExtendedEbcdicDbcsCharset(
    val name: String,
    val aliases: List<String>,
    val b2Min: Int,
    val b2Max: Int,
    val b2cSB: IntArray,
    val b2c: List<B2CRow>,
    val c2bSize: Int,
    val b2cNR: IntArray,
    val c2bNR: IntArray,
    val composites: List<CompositeCharsEntry>,
  )

  internal class B2CRow(
    val index: Int,
    val cps: IntArray,
  )

  internal class CompositeCharsEntry(
    val bs: Int,
    val cp: Int,
    val cp2: Int,
  )

  @get:Nested
  protected val sbcs = project.container<CharsetOptions>()

  @get:Nested
  protected val ebcdicDbcs = project.container<EbcdicDbcsCharsetOptions>()

  @get:Nested
  protected val extendedEbcdicDbcs = project.container<ExtendedEbcdicDbcsCharsetOptions>()

  /**
   * The directory where source `.map` files are localed.
   *
   * It may also contain `.c2b` and `.nr` files.
   */
  @get:InputDirectory
  @get:NormalizeLineEndings
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val mappingsDir: DirectoryProperty

  /**
   * The directory where to output generated charsets for all platforms.
   */
  @get:OutputDirectory
  abstract val commonDir: DirectoryProperty

  /**
   * The directory where to output generated charsets for all non-JVM platforms.
   */
  @get:OutputDirectory
  abstract val nonJvmDir: DirectoryProperty

  /**
   * The directory where to output generated charsets for the JVM platform.
   */
  @get:OutputDirectory
  abstract val jvmDir: DirectoryProperty

  /**
   * The package name to use for the generated charsets.
   */
  @get:Input
  abstract val packageName: Property<String>

  /**
   * Adds a Single Byte Character Set to the list of charsets to generate.
   */
  fun sbcs(name: String, configure: CharsetOptions.() -> Unit = {}) {
    val item = sbcs.create(name)
    item.configure()
  }

  /**
   * Adds an EBCDIC Double Byte Character Set to the list of charsets to generate.
   *
   * @param b2Min The smallest legal second byte value, included
   * @param b2Max The largest legal second byte value, included
   */
  fun ebcdicDbcs(name: String, b2Min: Int, b2Max: Int, configure: EbcdicDbcsCharsetOptions.() -> Unit = {}) {
    val item = ebcdicDbcs.create(name)
    item.b2Min.set(b2Min)
    item.b2Max.set(b2Max)
    item.configure()
  }

  /**
   * Adds an _extended_ EBCDIC Double Byte Character Set to the list of charsets to generate.
   *
   * The _extended_ variant supports surrogate pairs and composite character sequences.
   *
   * @param b2Min The smallest legal second byte value, included
   * @param b2Max The largest legal second byte value, included
   */
  fun extendedEbcdicDbcs(
    name: String,
    b2Min: Int,
    b2Max: Int,
    configure: ExtendedEbcdicDbcsCharsetOptions.() -> Unit = {},
  ) {
    val item = extendedEbcdicDbcs.create(name)
    item.b2Min.set(b2Min)
    item.b2Max.set(b2Max)
    item.configure()
  }

  @TaskAction
  protected fun execute() {
    val mappingsDir = mappingsDir.get().asFile
    val commonDir = commonDir.get().asFile
    val nonJvmDir = nonJvmDir.get().asFile
    val jvmDir = jvmDir.get().asFile
    val charsets = extendedEbcdicDbcs.asSequence() + ebcdicDbcs.asSequence() + sbcs.asSequence()

    val packageName = packageName.get()
    val classNames = mutableListOf<String>()

    for (options in charsets) {
      val outDir = if (options.common.get()) {
        // Output the generated declaration in the common source set
        commonDir
      } else {
        // Generate the expect declaration in the common source set
        generateExpectCharset(commonDir, options)

        // Generate the JVM-specific declaration
        generateJvmCharset(jvmDir, options)

        // Output the generated declaration in the nonJvm source set
        nonJvmDir
      }

      val className = when (options) {
        is ExtendedEbcdicDbcsCharsetOptions -> generateExtendedEbcdicDbcs(mappingsDir, outDir, options)
        is EbcdicDbcsCharsetOptions -> generateEbcdicDbcs(mappingsDir, outDir, options)
        is CharsetOptions -> generateSbcs(mappingsDir, outDir, options)
        else -> error("Unknown options type")
      }

      classNames += "$packageName.$className"
    }

    generateCharsetRegistrar(commonDir, packageName, classNames)
  }

  private fun generateCharsetRegistrar(baseDir: File, packageName: String, classNames: List<String>) {
    val template = freemarker.getTemplate("CharsetProvider.ftl")
    val data = mapOf(
      "packageName" to packageName,
      "classNames" to classNames,
    )

    val file = baseDir.resolve("CharsetProvider.kt")
    val writer = file.bufferedWriter()
    writer.use {
      template.process(data, writer)
    }
  }

  private fun generateExpectCharset(baseDir: File, options: CharsetOptions) {
    val template = freemarker.getTemplate("CharsetExpect.ftl")
    val packageName = packageName.get()
    val className = options.className.getOrElse(options.name)
    val data = mapOf(
      "packageName" to packageName,
      "className" to className,
    )

    val file = baseDir.resolve("$className.kt")
    val writer = file.bufferedWriter()
    writer.use {
      template.process(data, writer)
    }
  }

  private fun generateJvmCharset(baseDir: File, options: CharsetOptions) {
    val template = freemarker.getTemplate("CharsetJvm.ftl")
    val packageName = packageName.get()
    val charsetName = options.name
    val className = options.className.getOrElse(charsetName)
    val data = mapOf(
      "packageName" to packageName,
      "className" to className,
      "charsetName" to charsetName,
    )

    val file = baseDir.resolve("$className.kt")
    val writer = file.bufferedWriter()
    writer.use {
      template.process(data, writer)
    }
  }

  @Suppress("FoldInitializerAndIfToElvis")
  private fun generateSbcs(mappingsDir: File, baseDir: File, options: CharsetOptions): String {
    val charsetName = options.name
    val mapFile = mappingsDir.resolve("$charsetName.map")
    val mapEntries = mapFile.readMappingEntries()

    if (mapEntries == null) {
      throw GradleException("The $charsetName.map file does not exist at: $mapFile")
    }

    val entries = TreeSet(mapEntries)

    if (entries.size > 256) {
      throw GradleException("The SBCS b2c mappings must be 256 or less")
    }

    val activeHighs = mutableSetOf<Int>()

    for ((_, cp) in entries) {
      val high = cp.firstByte()
      activeHighs.add(high)
    }

    // c2b non-roundtrip overwrites contribute to the c2b array size
    val c2bFile = mappingsDir.resolve("$charsetName.c2b")
    val c2bEntries = c2bFile.readMappingEntries() ?: emptyList()

    for ((_, cp) in c2bEntries) {
      val high = cp.firstByte()
      activeHighs.add(high)
    }

    // b2c non-roundtrip entries
    val nrFile = mappingsDir.resolve("$charsetName.nr")
    val nrEntries = nrFile.readMappingEntries() ?: emptyList()

    // Initialize the b2cStr string with \uFFFD for all indexes
    val unicodeValues = MutableList(256) { UNMAPPABLE_DECODING }

    // Overwrite the default \uFFFD values with the mapping entries.
    // Depending on the charset, not all defaults may be overwritten.
    for ((bs, cp) in entries) {
      unicodeValues[bs] = cp.toUnicodeChar()
    }

    val template = freemarker.getTemplate("CharsetSbcs.ftl")
    val data = mutableMapOf<String, Any>()

    // Custom functions
    data["toHex"] = HexFunction
    data["toUnicode"] = UnicodeFunction

    val packageName = packageName.get()
    val className = options.className.getOrElse(charsetName)
    data["packageName"] = packageName
    data["className"] = className

    // Charset information
    val aliases = options.aliases.getOrElse(emptyList())
    val b2cStr = unicodeValues.joinToString(separator = "")
    val c2bSize = activeHighs.size * 256
    data["charset"] = SbcsCharset(
      name = charsetName,
      aliases = aliases,
      b2cStr = b2cStr,
      c2bSize = c2bSize,
      b2cNR = nrEntries,
      c2bNR = c2bEntries,
    )

    val file = baseDir.resolve("$className.kt")
    val writer = file.bufferedWriter()
    writer.use {
      template.process(data, writer)
    }

    return className
  }

  @Suppress("FoldInitializerAndIfToElvis")
  private fun generateEbcdicDbcs(mappingsDir: File, baseDir: File, options: EbcdicDbcsCharsetOptions): String {
    val charsetName = options.name
    val mapFile = mappingsDir.resolve("$charsetName.map")
    val mapEntries = mapFile.readMappingEntries()

    if (mapEntries == null) {
      throw GradleException("The $charsetName.map file does not exist at: $mapFile")
    }

    val entries = TreeSet(mapEntries)
    val sbUnicodeValues = MutableList(256) { UNMAPPABLE_DECODING }
    var hasSingleBytes = false

    for ((bs, cp) in entries) {
      // We must consider only single byte entries
      if (bs > 255) {
        break
      }

      val i = bs and 0xFF /* to unsigned */
      val unicode = cp.toUnicodeChar()
      sbUnicodeValues[i] = unicode

      if (!hasSingleBytes) {
        hasSingleBytes = unicode != UNMAPPABLE_DECODING
      }
    }

    val b2Min = options.b2Min.get()
    val b2Max = options.b2Max.get()
    val b2cStrEntries = generateB2CStr(entries.toList(), b2Min, b2Max)
    val activeHighs = mutableSetOf<Int>()

    for ((_, cp) in entries) {
      val high = cp shr 8
      activeHighs.add(high)
    }

    // c2b non-roundtrip overwrites contribute to the c2b array size
    val c2bFile = mappingsDir.resolve("$charsetName.c2b")
    val c2bEntries = c2bFile.readMappingEntries() ?: emptyList()

    for ((_, cp) in c2bEntries) {
      val high = cp shr 8
      activeHighs.add(high)
    }

    // The first 256 indexes are reserved for unmappable segments
    val c2bSize = (1 + activeHighs.size) * 256

    val nrFile = mappingsDir.resolve("$charsetName.nr")
    val nrEntries = nrFile.readMappingEntries() ?: emptyList()

    val b2cNR = nrEntries.joinToString(separator = "") {
      it.bs.toUnicodeChar() + it.cp.toUnicodeChar()
    }

    val c2bNR = c2bEntries.joinToString(separator = "") {
      it.bs.toUnicodeChar() + it.cp.toUnicodeChar()
    }

    val template = freemarker.getTemplate("CharsetEbcdicDbcs.ftl")
    val data = mutableMapOf<String, Any>()

    // Custom functions
    data["toHex"] = HexFunction
    data["toUnicode"] = UnicodeFunction

    val packageName = packageName.get()
    val className = options.className.getOrElse(charsetName)
    data["packageName"] = packageName
    data["className"] = className

    // Charset information
    val aliases = options.aliases.getOrElse(emptyList())
    val b2cSBStr = if (hasSingleBytes) {
      sbUnicodeValues.joinToString(separator = "")
    } else {
      ""
    }

    data["charset"] = EbcdicDbcsCharset(
      name = charsetName,
      aliases = aliases,
      b2Min = b2Min,
      b2Max = b2Max,
      b2cStrEntries = b2cStrEntries,
      b2cSBStr = b2cSBStr,
      c2bSize = c2bSize,
      b2cNR = b2cNR,
      c2bNR = c2bNR,
    )

    val file = baseDir.resolve("$className.kt")
    val writer = file.bufferedWriter()
    writer.use {
      template.process(data, writer)
    }

    return className
  }

  @Suppress("FoldInitializerAndIfToElvis")
  private fun generateExtendedEbcdicDbcs(mappingsDir: File, baseDir: File, options: EbcdicDbcsCharsetOptions): String {
    val charsetName = options.name
    val mapFile = mappingsDir.resolve("$charsetName.map")
    val mapEntries = mapFile.readMappingEntries2()

    if (mapEntries == null) {
      throw GradleException("The $charsetName.map file does not exist at: $mapFile")
    }

    // 1. Filter out composite chars
    // 2. Sort by byte sequence
    val sortedMapEntries = mapEntries
      .filter { it.cs.size == 1 }
      .sortedBy { it.bs }
      .map { MappingEntry(it.bs, it.cs[0]) }

    val b2Min = options.b2Min.get()
    val b2Max = options.b2Max.get()

    // Discard all 1-byte entries as the b2c table must handle double bytes only.
    val dbcsEntries = sortedMapEntries.filter { it.bs > 255 }
    val b2c = generateB2CArray(dbcsEntries, b2Min, b2Max)

    // Prepare the list of characters to build the b2cSBStr value.
    // This string will contain all the single byte -> char mappings.
    val b2cSB = IntArray(256) { 0xFFFD }
    var hasSingleBytes = false

    for ((bs, cp) in sortedMapEntries) {
      // We must consider only single byte entries
      if (bs > 255) {
        break
      }

      val i = bs and 0xFF /* to unsigned */
      b2cSB[i] = cp

      if (!hasSingleBytes) {
        hasSingleBytes = cp != 0xFFFD
      }
    }

    val c2bFile = mappingsDir.resolve("$charsetName.c2b")
    val c2bEntries = c2bFile.readMappingEntries2() ?: emptyList()
    val sortedC2bEntries = c2bEntries
      .filter { it.cs.size == 1 }
      .sortedBy { it.bs }
      .map { MappingEntry(it.bs, it.cs[0]) }

    val cb2NR = sortedC2bEntries.flatMap { listOf(it.bs, it.cp) }.toIntArray()
    val activeHighs = mutableSetOf<Int>()

    for ((_, cp) in sortedMapEntries) {
      val high = cp ushr 8
      activeHighs.add(high)
    }

    // c2b non-roundtrip mappings contribute to the c2b array size
    for ((_, cp) in sortedC2bEntries) {
      val high = cp ushr 8
      activeHighs.add(high)
    }

    // The first 256 indexes are reserved for unmappable segments
    val c2bSize = (1 + activeHighs.size) * 256

    val nrFile = mappingsDir.resolve("$charsetName.nr")
    val nrEntries = nrFile.readMappingEntries2() ?: emptyList()
    val sortedNrEntries = nrEntries
      .filter { it.cs.size == 1 }
      .sortedBy { it.bs }
      .map { MappingEntry(it.bs, it.cs[0]) }

    val b2cNR = sortedNrEntries.flatMap { listOf(it.bs, it.cp) }.toIntArray()

    val template = freemarker.getTemplate("CharsetExtendedEbcdicDbcs.ftl")
    val data = mutableMapOf<String, Any>()

    // Custom functions
    data["toHex"] = HexFunction
    data["toUnicode"] = UnicodeFunction

    val className = options.className.getOrElse(charsetName)
    data["packageName"] = packageName.get()
    data["className"] = className
    data["isCommon"] = options.common.get()

    // Charset information
    val aliases = options.aliases.getOrElse(emptyList())
    val composites = mapEntries
      .filter { it.cs.size > 1 }
      .sortedBy { it.bs }
      .map {
        CompositeCharsEntry(
          bs = it.bs,
          cp = it.cs[0],
          cp2 = it.cs[1],
        )
      }

    data["charset"] = ExtendedEbcdicDbcsCharset(
      name = charsetName,
      aliases = aliases,
      b2Min = b2Min,
      b2Max = b2Max,
      b2cSB = b2cSB,
      b2c = b2c,
      c2bSize = c2bSize,
      b2cNR = b2cNR,
      c2bNR = cb2NR,
      composites = composites,
    )

    val file = baseDir.resolve("$className.kt")
    val writer = file.bufferedWriter()
    writer.use {
      template.process(data, writer)
    }

    return className
  }

  private fun generateB2CArray(entries: List<MappingEntry>, b2Min: Int, b2Max: Int): List<B2CRow> {
    // A 256x256 table, where a row represents the first byte, and a column represents the second byte
    val b2c = TreeMap<Int, IntArray>()

    // We now loop the double byte entries, where each unique first byte
    // value represents a b2c array index.
    // For example in IBM1390, the first double byte mapping is 0x4040,
    // which means we need to insert all in-range (b2Min-b2Max) 0x40XX
    // codepoints at b2c[0x40], e.g. b2c[0x40] = intArrayOf(0x3000, 0x03B1, ...)
    var lead = -1
    var i = 0

    while (i < entries.size) {
      val (bs, cp) = entries[i++]
      check(bs > 0xFF)

      val b1 = bs.firstByte()
      val b2 = bs.secondByte()
      val b2cRow = IntArray(b2Max - b2Min + 1) { 0xFFFD /* unmappable decoding */ }

      if (b2 in b2Min..b2Max) {
        b2cRow[b2 - b2Min] = cp
      }

      if (b1 != lead) {
        lead = b1

        while (i < entries.size) {
          val (sbs, scp) = entries[i++]

          if (b1 != sbs.firstByte()) {
            i--
            break
          }

          val sb2 = sbs.secondByte()

          if (sb2 in b2Min..b2Max) {
            b2cRow[sb2 - b2Min] = scp
          }
        }

        b2c[b1] = b2cRow
      }
    }

    return b2c.map { B2CRow(it.key, it.value) }
  }

  private fun generateB2CStr(entries: List<MappingEntry>, b2Min: Int, b2Max: Int): List<String> {
    // Discard all 1-byte entries as we are dealing with DBCS here.
    // Entries MUST be already ordered by byte sequence here.
    val dbcsEntries = entries.filter { it.bs > 255 }

    // This array represents the slots of String[] b2cStr
    val b2cStr = MutableList(256) { "null" }

    // We now loop the DBCS entries, where each unique lead byte
    // value represents a b2cStr array index. For example in IBM930,
    // the first DBCS entry is 0x4040, which means we need to insert
    // all in-range (b2Min-b2Max) 0x40XX values at b2cStr index 0x40.
    var lastLead = -1
    var i = 0

    while (i < dbcsEntries.size) {
      val entry = dbcsEntries[i++]
      val b1 = entry.bs.firstByte()
      val b2 = entry.bs.secondByte()
      val b2Str = Array(b2Max - b2Min + 1) { UNMAPPABLE_DECODING }

      if (b2 in b2Min..b2Max) {
        b2Str[b2 - b2Min] = entry.cp.toUnicodeChar()
      }

      if (b1 != lastLead) {
        lastLead = b1

        while (i < dbcsEntries.size) {
          val subEntry = dbcsEntries[i++]
          val sb1 = subEntry.bs.firstByte()

          if (sb1 != b1) {
            i--
            break
          }

          val sb2 = subEntry.bs.secondByte()

          if (sb2 in b2Min..b2Max) {
            b2Str[sb2 - b2Min] = subEntry.cp.toUnicodeChar()
          }
        }

        b2cStr[b1] = b2Str.joinToString(separator = "", prefix = "\"", postfix = "\"")
      }
    }

    return b2cStr
  }

  private fun File.readMappingEntries(): List<MappingEntry>? {
    if (!this.exists()) {
      return null
    }

    return this.readLines(Charsets.UTF_8)
      .map(String::trim)
      .filterNot {
        // Comment line, or empty
        it.startsWith("#") || it.isEmpty()
      }
      .map(::parseMappingEntry)
  }

  private fun parseMappingEntry(line: String): MappingEntry {
    var (bsHex, cpHex) = line.split(wsRegex)

    if (bsHex.startsWith("0x")) {
      bsHex = bsHex.drop(2)
    }

    if (cpHex.startsWith("U+")) {
      cpHex = cpHex.drop(2)
    }

    return MappingEntry(
      bs = Integer.parseInt(bsHex, 16),
      cp = Integer.parseInt(cpHex, 16),
    )
  }

  private fun File.readMappingEntries2(): List<MappingEntry2>? {
    if (!this.exists()) {
      return null
    }

    return this.readLines(Charsets.UTF_8)
      .map(String::trim)
      .filterNot {
        // Comment line, or empty
        it.startsWith("#") || it.isEmpty()
      }
      .map(::parseMappingEntry2)
  }

  private fun parseMappingEntry2(line: String): MappingEntry2 {
    var (bsHex, cpHex) = line.split(wsRegex)

    if (bsHex.startsWith("0x")) {
      bsHex = bsHex.drop(2)
    }

    if (cpHex.startsWith("U+")) {
      cpHex = cpHex.drop(2)
    }

    val cs = cpHex.split("+")
      .map { Integer.parseInt(it, 16) }
      .toIntArray()

    return MappingEntry2(
      bs = Integer.parseInt(bsHex, 16),
      cs = cs,
    )
  }
}

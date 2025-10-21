import com.lppedd.kotlinx.charset.GenerateCharsetTask
import com.lppedd.kotlinx.charset.setupPom
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
  id("kotlinx-charset-kmp")
  id("kotlinx-charset-maven")
}

description = "Provides support for EBCDIC charsets"

mavenPublishing {
  setupPom(project)
}

val generateCharsets = tasks.register<GenerateCharsetTask>("generateCharsets") {
  mappingsDir = layout.projectDirectory.dir("charsets")
  commonDir = layout.buildDirectory.dir("generatedCharsetsCommon")
  nonJvmDir = layout.buildDirectory.dir("generatedCharsetsNonJvm")
  jvmDir = layout.buildDirectory.dir("generatedCharsetsJvm")
  packageName = "com.lppedd.kotlinx.charset.ebcdic"

  // Latin 1 US/Canada
  sbcs("IBM037") {
    aliases = listOf(
      "cp037",
      "ebcdic-cp-us",
      "ebcdic-cp-ca",
      "ebcdic-cp-wt",
      "ebcdic-cp-nl",
      "csIBM037",
      "cs-ebcdic-cp-us",
      "cs-ebcdic-cp-ca",
      "cs-ebcdic-cp-wt",
      "cs-ebcdic-cp-nl",
      "ibm-037",
      "ibm-37",
      "cpibm37",
      "037",
    )
  }

  // Latin 1/Open Systems
  sbcs("IBM1047") {
    aliases = listOf("cp1047", "ibm-1047", "1047")
  }

  // Latin 1 Austria/Germany
  sbcs("IBM273") {
    aliases = listOf("cp273", "ibm-273", "273")
  }

  // Latin 1 Finland/Sweden
  sbcs("IBM278") {
    aliases = listOf("cp278", "ibm-278", "278", "ebcdic-sv", "ebcdic-cp-se", "csIBM278")
  }

  // Latin 1 France
  sbcs("IBM297") {
    aliases = listOf("cp297", "ibm-297", "297", "ebcdic-cp-fr", "cpibm297", "csIBM297")
  }

  // Latin 1 Austria/Germany with Euro sign (like IBM273, but 0x9F = u00A4 -> u20AC)
  sbcs("IBM01141") {
    aliases = listOf("cp1141", "ccsid01141", "1141", "ebcdic-de-273+euro", "ibm1141", "ibm-1141")
    className = "IBM1141"
  }

  // Latin 1 Finland/Sweden with Euro sign (like IBM278, but 0x5A = u00A4 -> u20AC)
  sbcs("IBM01143") {
    aliases = listOf(
      "cp1143",
      "ccsid01143",
      "cp01143",
      "1143",
      "ebcdic-fi-278+euro",
      "ebcdic-se-278+euro",
      "ibm1143",
      "ibm-1143"
    )

    className = "IBM1143"
  }

  // Latin 1 France with Euro sign (like IBM297, but 0x9F = u00A4 -> u20AC)
  sbcs("IBM01147") {
    aliases = listOf("cp1147", "ccsid01147", "cp01147", "1147", "ebcdic-fr-277+euro", "ibm1147", "ibm-1147")
    className = "IBM1147"
  }

  // Japanese - halfwidth Katakana, fullwidth Katakana, Hiragana and Kanji
  // Single byte portion is IBM290
  ebcdicDbcs("x-IBM930", b2Min = 0x40, b2Max = 0xFE) {
    aliases = listOf("cp930", "ibm930", "ibm-930", "930")
    className = "IBM930"

    // The JDK seems to be incorrectly encoding replacements for unmappable code points
    common = true
  }

  // Traditional Chinese (Taiwan)
  ebcdicDbcs("x-IBM937", b2Min = 0x40, b2Max = 0xFE) {
    aliases = listOf("cp937", "ibm937", "ibm-937", "937")
    className = "IBM937"

    // The JDK seems to be incorrectly encoding replacements for unmappable code points
    common = true
  }

  // Like IBM930, but the single byte portion is IBM1041 (an extension of IBM290)
  ebcdicDbcs("x-IBM939", b2Min = 0x40, b2Max = 0xFE) {
    aliases = listOf("cp939", "ibm939", "ibm-939", "939")
    className = "IBM939"

    // The JDK seems to be incorrectly encoding replacements for unmappable code points
    common = true
  }

  // Extended Japanese support (similar to JIS X 0213)
  extendedEbcdicDbcs("x-IBM1390", b2Min = 0x40, b2Max = 0xFE) {
    aliases = listOf("cp1390", "ibm1390", "ibm-1390", "1390")
    className = "IBM1390"

    // JDKs do not seem to offer built-in support for IBM1390
    common = true
  }

  // Japanese, similar to IBM1390
  extendedEbcdicDbcs("x-IBM1399", b2Min = 0x40, b2Max = 0xFE) {
    aliases = listOf("cp1399", "ibm1399", "ibm-1399", "1399")
    className = "IBM1399"

    // JDKs do not seem to offer built-in support for IBM1390
    common = true
  }
}

tasks {
  withType<KotlinCompilationTask<*>>().configureEach {
    dependsOn(generateCharsets)
  }

  sourcesJar {
    dependsOn(generateCharsets)
  }
}

kotlin {
  sourceSets {
    commonMain {
      kotlin {
        srcDir(generateCharsets.flatMap { it.commonDir })
      }

      dependencies {
        api(projects.core)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlinxIO)
      }
    }

    jvmMain {
      kotlin {
        srcDir(generateCharsets.flatMap { it.jvmDir })
      }
    }

    nonJvmMain {
      kotlin {
        srcDir(generateCharsets.flatMap { it.nonJvmDir })
      }
    }
  }
}

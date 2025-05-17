import com.github.lppedd.kotlinx.charset.GenerateCharsetTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
  id("kotlinx-charset-kmp")
  id("kotlinx-charset-maven")
}

val generateCharsets = tasks.register<GenerateCharsetTask>("generateCharsets") {
  mappingsDir = layout.projectDirectory.dir("charsets")
  commonDir = layout.buildDirectory.dir("generatedCharsetsCommon")
  nonJvmDir = layout.buildDirectory.dir("generatedCharsetsNonJvm")
  jvmDir = layout.buildDirectory.dir("generatedCharsetsJvm")
  packageName = "com.github.lppedd.kotlinx.charset.ebcdic"

  // Latin 1 US/Canada
  sbcs("IBM037") {
    aliases = listOf(
      "cp037",
      "ibm037",
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
    aliases = listOf("cp273", "ibm273", "ibm-273", "273")
  }

  // Latin 1 France
  sbcs("IBM297") {
    aliases = listOf("cp297", "ibm297", "ibm-297", "297", "ebcdic-cp-fr", "cpibm297", "csIBM297")
  }

  // Latin 1 Austria/Germany with Euro sign (like IBM273, but 0x9F = u00A4 -> u20AC)
  sbcs("IBM01141") {
    aliases = listOf("cp1141", "ccsid01141", "1141", "ebcdic-de-273+euro", "ibm1141", "ibm-1141")
    className = "IBM1141"
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
  }

  // Like IBM930, but the single byte portion is IBM1041 (an extension of IBM290)
  ebcdicDbcs("x-IBM939", b2Min = 0x40, b2Max = 0xFE) {
    aliases = listOf("cp939", "ibm939", "ibm-939", "939")
    className = "IBM939"
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
  targets {
    configureEach {
      tasks.named(targetName + "SourcesJar").configure {
        dependsOn(generateCharsets)
      }
    }
  }

  sourceSets {
    commonMain {
      kotlin {
        srcDir(layout.buildDirectory.dir("generatedCharsetsCommon"))
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
        srcDir(layout.buildDirectory.dir("generatedCharsetsJvm"))
      }
    }

    named("nonJvmMain").configure {
      kotlin {
        srcDir(layout.buildDirectory.dir("generatedCharsetsNonJvm"))
      }
    }
  }
}

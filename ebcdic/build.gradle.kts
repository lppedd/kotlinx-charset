import com.github.lppedd.kotlinx.charset.GenerateCharsetTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
  id("kotlinx-charset-kmp")
  id("kotlinx-charset-maven")
}

val generateCharsets = tasks.register<GenerateCharsetTask>("generateCharsets") {
  mappingsDir = layout.projectDirectory.dir("charsets")
  expectDir = layout.buildDirectory.dir("generatedCharsetsExpect")
  nonJvmDir = layout.buildDirectory.dir("generatedCharsetsNonJvm")
  jvmDir = layout.buildDirectory.dir("generatedCharsetsJvm")
  packageName = "com.github.lppedd.kotlinx.charset.ebcdic"

  // Latin 1 US/Canada
  sbcs("IBM037") {
    aliases = listOf("cp037", "ibm-037", "037")
  }

  // Latin 1/Open Systems
  sbcs("IBM1047") {
    aliases = listOf("cp1047", "ibm-1047", "x-IBM1047", "1047")
  }

  // Latin 1 Austria/Germany
  sbcs("IBM273") {
    aliases = listOf("cp273", "ibm273", "ibm-273", "273")
  }

  // Latin 1 Austria/Germany with Euro sign (like IBM273, but u00A4 -> u20AC)
  sbcs("IBM01141") {
    aliases = listOf("cp1141", "ccsid01141", "1141", "ebcdic-de-273+euro", "ibm1141", "ibm-1141")
    className = "IBM1141"
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
        srcDir(layout.buildDirectory.dir("generatedCharsetsExpect"))
      }

      dependencies {
        api(projects.core)
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

import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask

plugins {
  id("kotlinx-charset-kmp")
  alias(libs.plugins.antlrKotlin)
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
  dependsOn("cleanGenerateKotlinGrammarSource")

  // ANTLR .g4 files are under ucm/antlr
  // Only include *.g4 files. This allows tools (e.g., IDE plugins)
  // to generate temporary files inside the base path
  source = fileTree(layout.projectDirectory.dir("antlr")) {
    include("**/*.g4")
  }

  // We want the generated source files to have this package name
  val pkgName = "com.lppedd.kotlinx.charset.ucm.parser"
  packageName = pkgName

  // We want visitors alongside listeners.
  // The Kotlin target language is implicit, as is the file encoding (UTF-8)
  arguments = listOf("-visitor")

  // Generated files are outputted inside build/generatedAntlr/{packageName}
  val outDir = "generatedAntlr/${pkgName.replace(".", "/")}"
  outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}

kotlin {
  sourceSets {
    commonMain {
      kotlin {
        srcDir(generateKotlinGrammarSource)
      }

      dependencies {
        implementation(libs.antlrKotlin)
      }
    }
  }
}

tasks {
  clean {
    delete(generateKotlinGrammarSource)
  }
}

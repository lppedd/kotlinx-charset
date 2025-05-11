import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
  id("kotlinx-charset-kmp")
  alias(libs.plugins.antlrKotlin)
}

kotlin {
  sourceSets {
    commonMain {
      kotlin {
        srcDir(layout.buildDirectory.dir("generatedAntlr"))
      }

      dependencies {
        implementation(libs.antlrKotlin)
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
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
  val pkgName = "com.github.lppedd.kotlinx.charset.ucm.parser"
  packageName = pkgName

  // We want visitors alongside listeners.
  // The Kotlin target language is implicit, as is the file encoding (UTF-8)
  arguments = listOf("-visitor")

  // Generated files are outputted inside build/generatedAntlr/{packageName}
  val outDir = "generatedAntlr/${pkgName.replace(".", "/")}"
  outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}

tasks.withType<KotlinCompilationTask<*>> {
  dependsOn(generateKotlinGrammarSource)
}

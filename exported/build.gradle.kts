import org.gradle.internal.os.OperatingSystem

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("kotlinx-charset-maven")
  alias(libs.plugins.npmPublish)
}

kotlin {
  // Mandate explicit visibility modifiers
  explicitApiWarning()

  js {
    nodejs()
    binaries.library()
    generateTypeScriptDefinitions()

    compilerOptions {
      target = "es2015"
      freeCompilerArgs.addAll(
        "-Xes-arrow-functions", // K/JS bug: requires explicit activation in 2.2.0
        "-Xir-generate-inline-anonymous-functions",
        "-Xir-property-lazy-initialization=false",
        "-Xgenerate-polyfills=false",
      )
    }
  }

  sourceSets {
    configureEach {
      languageSettings {
        optIn("kotlin.js.ExperimentalJsExport")
      }
    }

    commonMain {
      dependencies {
        api(projects.core)
        implementation(projects.ebcdic)
      }
    }
  }
}

npmPublish {
  // https://github.com/mpetuska/npm-publish/issues/187
  if (OperatingSystem.current().isWindows) {
    val nodePath = System.getenv("NODE_HOME")

    if (nodePath != null) {
      nodeHome = project.objects.directoryProperty().fileValue(File(nodePath))
      nodeBin = nodeHome.file("node.exe")
      npmBin = nodeHome.file("node_modules/npm/bin/npm-cli.js")
    }
  }

  registries {
    npmjs {}
  }

  packages {
    // "js" is the name taken from the Kotlin/JS target
    named("js") {
      scope = "lppedd"
      packageName = "kotlinx-charset"
      readme = rootProject.layout.projectDirectory.file("README.md")

      // Improve published package discoverability
      packageJson {
        description = "Minimal support for charset encoding and decoding"
        license = "MIT"
        keywords = setOf(
          "ebcdic",
          "charset",
          "encoder",
          "decoder",
          "support",
          "kotlin",
          "multiplatform",
        )

        repository {
          type = "git"
          url = "https://github.com/lppedd/kotlinx-charset.git"
        }

        bugs {
          url = "https://github.com/lppedd/kotlinx-charset/issues"
        }
      }

      files {
        // Include the license file in the published package
        from(rootProject.layout.projectDirectory.file("LICENSE"))
      }
    }
  }
}

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
  if (OperatingSystem.current().isWindows) {
    // https://github.com/mpetuska/npm-publish/issues/187
    nodeHome = project.objects.directoryProperty().fileValue(File(System.getenv("NODE_HOME")))
    nodeBin = nodeHome.file("node.exe")
    npmBin = nodeHome.file("node_modules/npm/bin/npm-cli.js")
  }

  registries {
    npmjs {
      //
    }
  }

  packages {
    // "js" is the name taken from the Kotlin/JS target
    named("js") {
      scope = "lppedd"
      packageName = "kotlinx-charset"
      readme = project.layout.projectDirectory.file("../README.md")
    }
  }
}

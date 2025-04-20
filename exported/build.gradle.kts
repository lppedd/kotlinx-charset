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
  dry = true

  registries {
    register("npmjs") {
      uri = uri("https://registry.npmjs.org")
      authToken = "obfuscated"
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

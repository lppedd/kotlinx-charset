@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("multiplatform")
}

kotlin {
  // Mandate explicit visibility modifiers
  explicitApiWarning()

  // Allow using expect-actual classes
  compilerOptions {
    freeCompilerArgs.add("-Xexpect-actual-classes")
  }

  //
  // Targets
  //

  jvm {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_1_8
    }
  }

  js {
    nodejs()
    useCommonJs()
    compilerOptions {
      useEsClasses = true
      freeCompilerArgs.addAll(
        "-Xes-arrow-functions", // K/JS bug: requires explicit activation in 2.2.0
        "-Xir-generate-inline-anonymous-functions",
        "-Xir-property-lazy-initialization=false",
        "-Xgenerate-polyfills=false",
      )
    }
  }

  wasmJs {
    nodejs()
  }

  wasmWasi {
    nodejs()
  }

  //
  // Tier 1
  //
  // macOS host only
  macosArm64()
  iosSimulatorArm64()
  iosArm64()

  //
  // Tier 2
  //
  linuxX64()
  linuxArm64()

  // macOS host only
  watchosSimulatorArm64()
  watchosArm64()
  tvosSimulatorArm64()
  tvosArm64()

  //
  // Tier 3
  //
  mingwX64()
  androidNativeArm32()
  androidNativeArm64()
  androidNativeX86()
  androidNativeX64()

  // macOS host only
  iosX64()
  watchosDeviceArm64()

  //
  // Deprecated - scheduled for removal
  //
  @Suppress("DEPRECATION") run {
    watchosArm32()
    macosX64()
    watchosX64()
    tvosX64()
  }

  // Use a customized hierarchy to split JVM-specific declarations
  // from the rest of the supported platforms.
  //
  //            +--------------+
  //            |  commonMain  |
  //            +-------+------+
  //                    |
  //                    |
  //        +-----------+-----------+
  //        |                       |
  //        |                       |
  // +------+-------+      +--------+-----+
  // |   jvmMain    |      |  nonJvmMain  |
  // +--------------+      +--------------+
  //                        JS/WASM/Native
  applyHierarchyTemplate {
    common {
      group("jvm") {
        withJvm()
      }

      group("nonJvm") {
        withJs()
        withWasmJs()
        withWasmWasi()
        withNative()
      }
    }
  }

  sourceSets {
    commonTest {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
}

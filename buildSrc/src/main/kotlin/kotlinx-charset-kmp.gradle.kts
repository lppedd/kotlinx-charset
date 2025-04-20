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
  }

  wasmJs {
    nodejs()
  }

  wasmWasi {
    nodejs()
  }

  // Tier 1
  macosX64()
  macosArm64()
  iosSimulatorArm64()
  iosX64()
  iosArm64()

  // Tier 2
  linuxX64()
  linuxArm64()
  watchosSimulatorArm64()
  watchosX64()
  watchosArm32()
  watchosArm64()
  tvosSimulatorArm64()
  tvosX64()
  tvosArm64()

  // Tier 3
  mingwX64()
  watchosDeviceArm64()

  // Deprecated.
  // Should follow the same route as official Kotlin libraries
  @Suppress("DEPRECATION")
  linuxArm32Hfp()

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
}

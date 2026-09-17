plugins {
  `kotlin-dsl`
}

kotlin {
  jvmToolchain {
    languageVersion = JavaLanguageVersion.of(25)
  }
}

dependencies {
  implementation(libs.kotlinGradlePlugin)
  implementation(libs.mavenPublishPlugin)
  implementation(libs.freemarker)
}

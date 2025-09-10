plugins {
  `kotlin-dsl`
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  implementation(libs.kotlinGradlePlugin)
  implementation(libs.mavenPublishPlugin)
  implementation(libs.freemarker)
}

plugins {
  `kotlin-dsl`
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  implementation(libs.kotlinGradlePlugin)
  implementation(libs.mavenPublish)
  implementation(libs.freemarker)
}

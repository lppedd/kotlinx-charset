plugins {
  kotlin("jvm")
  application
}

dependencies {
  implementation(projects.ucm)
  implementation(libs.clikt)
}

application {
  mainClass = "com.github.lppedd.kotlinx.charset.ucmcli.Main"
}

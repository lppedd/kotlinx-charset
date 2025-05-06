plugins {
  id("com.vanniktech.maven.publish")
}

publishing {
  repositories {
    maven {
      name = "githubPackages"
      url = uri("https://maven.pkg.github.com/lppedd/kotlinx-charset")
      credentials(PasswordCredentials::class)
    }
  }
}

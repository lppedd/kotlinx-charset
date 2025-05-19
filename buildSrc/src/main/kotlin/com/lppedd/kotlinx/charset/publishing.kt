package com.lppedd.kotlinx.charset

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project

/**
 * Sets up the project's POM to be able to publish on Maven Central.
 */
fun MavenPublishBaseExtension.setupPom(project: Project) {
  pom {
    name.set(project.name)
    description.set(project.description)
    url.set("https://github.com/lppedd/kotlinx-charset")

    scm {
      connection.set("scm:git:https://github.com/lppedd/kotlinx-charset.git")
      developerConnection.set("scm:git:git@github.com:lppedd/kotlinx-charset.git")
      url.set("https://github.com/lppedd/kotlinx-charset.git")
    }

    licenses {
      license {
        name.set("MIT License")
        url.set("https://github.com/lppedd/kotlinx-charset/blob/master/LICENSE")
        distribution.set("repo")
      }
    }

    developers {
      developer {
        id.set("lppedd")
        name.set("Edoardo Luppi")
        email.set("lp.edoardo@gmail.com")
      }
    }
  }
}

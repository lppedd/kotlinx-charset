plugins {
  id("com.vanniktech.maven.publish")
}

mavenPublishing {
  publishToMavenCentral()
  signAllPublications()
  coordinates(
    groupId = project.group.toString(),
    artifactId = project.name,
    version = project.version.toString(),
  )
}

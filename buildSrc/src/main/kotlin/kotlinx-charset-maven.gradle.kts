import com.vanniktech.maven.publish.SonatypeHost

plugins {
  id("com.vanniktech.maven.publish")
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
  signAllPublications()
  coordinates(
    groupId = project.group.toString(),
    artifactId = project.name,
    version = project.version.toString(),
  )
}

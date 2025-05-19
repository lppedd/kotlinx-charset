import com.lppedd.kotlinx.charset.setupPom

plugins {
  id("kotlinx-charset-kmp")
  id("kotlinx-charset-maven")
}

description = "Provides the core interfaces to implement new charsets"

mavenPublishing {
  setupPom(project)
}

import org.jetbrains.kotlin.gradle.targets.js.npm.LockFileMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.npm.LockStoreTask

// Temporarily warn instead of failing the build,
// so we can debug what is going on under CI
tasks.withType<LockStoreTask>().configureEach {
  lockFileMismatchReport = LockFileMismatchReport.WARNING
}

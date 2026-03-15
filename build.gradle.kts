import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.sonarqube)
    jacoco
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

sonar {
    properties {
        property("sonar.projectName", "DocVault")
        property("sonar.projectKey", "com.johannjara.docvault")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.token", localProperties.getProperty("sonar.token") ?: "")
        property("sonar.sourceEncoding", "UTF-8")

        property("sonar.exclusions", "**/R.class, **/BuildConfig.*, **/Manifest*.*, **/*Test*.*, android/**/*.*, **/di/**, **/*Composable*.*, **/*Screen*.*, **/*Activity*.*, **/*Navigation*.*, **/*NavGraph*.*")
    }
}

subprojects {
    apply(plugin = "org.sonarqube")
    sonar {
        if (project.path == ":design") {
            isSkipProject = true
        } else {
            properties {
                property("sonar.sources", "src/main/java")
                if (file("src/test/java").exists()) {
                    property("sonar.tests", "src/test/java")
                }
                property("sonar.java.binaries", "build/intermediates/javac/debug/classes,build/tmp/kotlin-classes/debug,build/classes/kotlin/main")
                property("sonar.junit.reportPaths", "build/test-results/testDebugUnitTest,build/test-results/test")
                property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
            }
        }
    }
}

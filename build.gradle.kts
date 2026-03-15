import java.util.Properties

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

        property("sonar.coverage.exclusions", "**/R.class, **/BuildConfig.*, **/Manifest*.*, android/**/*.*, **/di/**, **/*Composable*.*, **/*Screen*.*, **/*Activity*.*, **/*Navigation*.*, **/*NavGraph*.*")
    }
}

subprojects {
    apply(plugin = "org.sonarqube")
    sonar {
        if (project.path == ":design") {
            isSkipProject = true
        } else {
            properties {
                val sources = mutableListOf<String>()
                if (file("src/main/java").exists()) sources.add("src/main/java")
                if (file("src/main/kotlin").exists()) sources.add("src/main/kotlin")
                if (sources.isNotEmpty()) property("sonar.sources", sources.joinToString(","))

                val tests = mutableListOf<String>()
                if (file("src/test/java").exists()) tests.add("src/test/java")
                if (file("src/test/kotlin").exists()) tests.add("src/test/kotlin")
                if (tests.isNotEmpty()) property("sonar.tests", tests.joinToString(","))

                property("sonar.java.binaries", "build/classes/kotlin/main,build/classes/java/main,build/intermediates/javac/debug/classes,build/tmp/kotlin-classes/debug,bin")
                
                property("sonar.junit.reportPaths", "build/test-results/test,build/test-results/testDebugUnitTest")

                property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
            }
        }
    }
}

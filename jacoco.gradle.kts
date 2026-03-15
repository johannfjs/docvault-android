apply(plugin = "jacoco")

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

val fileFilter = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/data/models/*",
    "**/*\$ViewInjector*.*",
    "**/*\$ViewBinder*.*",
    "**/*_MembersInjector.class",
    "**/Dagger*.*",
    "**/*_Factory.class",
    "**/*_Provide*Factory.class",
    "**/*_SingletonProxy.class",
    "**/*_HiltModules*.class",
    "**/*Hilt_*.class",
    "**/*_HiltComponents*.class",
    "**/*Composable*.*",
    "**/*Screen*.*",
    "**/*Activity*.*",
    "**/*Navigation*.*",
    "**/*NavGraph*.*"
)

val reportTaskName = "jacocoTestReport"

if (tasks.findByName(reportTaskName) == null) {
    tasks.register<JacocoReport>(reportTaskName) {
        val isAndroid = project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.library")
        
        dependsOn(if (isAndroid) "testDebugUnitTest" else "test")

        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        val kotlinTree = if (isAndroid) {
            fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
                setExcludes(fileFilter)
            }
        } else {
            fileTree("${project.layout.buildDirectory.get()}/classes/kotlin/main") {
                setExcludes(fileFilter)
            }
        }
        
        val javaTree = if (isAndroid) {
            fileTree("${project.layout.buildDirectory.get()}/intermediates/javac/debug/classes") {
                setExcludes(fileFilter)
            }
        } else {
            fileTree("${project.layout.buildDirectory.get()}/classes/java/main") {
                setExcludes(fileFilter)
            }
        }

        sourceDirectories.setFrom(files("${project.projectDir}/src/main/java", "${project.projectDir}/src/main/kotlin"))
        classDirectories.setFrom(files(kotlinTree, javaTree))
        
        executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
            include(
                "jacoco/testDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "jacoco/test.exec"
            )
        })
    }
}

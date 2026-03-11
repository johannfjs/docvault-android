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
    "**/*_HiltComponents*.class"
)

val debugTree = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
    setExcludes(fileFilter)
}

val mainSrc = "${project.projectDir}/src/main/java"

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
        include("jacoco/testDebugUnitTest.exec", "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

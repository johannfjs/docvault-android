plugins {
    alias(libs.plugins.kotlin.jvm)
    jacoco
}

apply(from = "../jacoco.gradle.kts")

tasks.named<Test>("test") {
    finalizedBy("jacocoTestReport")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.kluent)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}

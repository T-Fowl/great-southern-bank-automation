import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
    idea
}

group = "com.tfowl.gsb"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.tfowl.gsb.cli.GsbKt")

    findProperty("playwright.nodejs.path")?.let { path ->
        applicationDefaultJvmArgs = listOf("-Dplaywright.nodejs.path=$path")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.jsoup)
    implementation(libs.playwright)

    implementation(libs.picnic)

    implementation(libs.bundles.kotlinresult)

    implementation(libs.kotlinx.dataframe)
    implementation(libs.kotlinx.datetime)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.clikt)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}

java {
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
    }
}

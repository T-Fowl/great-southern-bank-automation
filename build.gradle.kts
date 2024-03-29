import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
}

group = "com.tfowl.gsb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")

    implementation("com.microsoft.playwright:playwright:1.27.0")

    implementation("org.jsoup:jsoup:1.14.3")

    implementation("com.jakewharton.picnic:picnic:0.5.0")

    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.13")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.13")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:2.0.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
}
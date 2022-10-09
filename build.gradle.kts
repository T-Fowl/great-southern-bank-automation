import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
}

group = "com.tfowl.gsb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.microsoft.playwright:playwright:1.27.0")

    implementation("com.jakewharton.picnic:picnic:0.6.0")

    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.16")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.16")

    implementation("org.jetbrains.kotlinx:dataframe:0.9.0-dev-1139")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("com.github.ajalt.clikt:clikt:3.5.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
}
plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.6"
}

group = "dev.badbird.processing"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.guava:guava:33.2.0-jre")
    implementation("commons-cli:commons-cli:1.8.0")
    implementation("org.ow2.asm:asm:9.7")
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest.attributes["Main-Class"] = "dev.badbird.processing.Main"
    manifest.attributes["Launcher-Agent-Class"] = "dev.badbird.processing.bullshit.JarLoader"
}
tasks.build {
    dependsOn("shadowJar")
}
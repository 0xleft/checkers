plugins {
    java
    application
}

group = "uk.wwws"
version = "1.0.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("uk.wwws.checkers")
    mainClass.set("uk.wwws.checkers.apps.entrypoints.ServerApp")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("org.jetbrains:annotations:23.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    implementation("org.apache.logging.log4j:log4j-api:2.22.0")
    implementation("org.apache.logging.log4j:log4j-core:2.22.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
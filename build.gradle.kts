plugins {
    java
    application
    groovy
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
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

tasks.register<Jar>("jarClient") {
    archiveBaseName.set("client")
    from(sourceSets.main.get().output)
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers.apps.entrypoints.ClientApp")
    }
}

tasks.register<Jar>("jarServer") {
    archiveBaseName.set("server")
    from(sourceSets.main.get().output)
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers.apps.entrypoints.ServerApp")
    }
}

tasks.register<Jar>("jarAI") {
    archiveBaseName.set("ai")
    from(sourceSets.main.get().output)
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers.apps.entrypoints.AIApp")
    }
}

tasks.register("buildAllJars") {
    dependsOn(tasks.named<Jar>("jarClient"))
    dependsOn(tasks.named<Jar>("jarServer"))
    dependsOn(tasks.named<Jar>("jarAI"))
}

application {
    mainModule.set("uk.wwws.checkers")
    mainClass.set("uk.wwws.checkers.apps.entrypoints.ClientApp")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing", "javafx.media")
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

jlink {
    launcher {
        name = "jarAILauncher" // name of the executable launcher
    }
}
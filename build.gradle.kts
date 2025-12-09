plugins {
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("io.freefair.aspectj.post-compile-weaving") version "8.6"
}

group = "uk.wwws.checkers.app"
version = "1.0.1"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-XprintProcessorInfo")
    options.compilerArgs.add("-XprintRounds")
}

tasks.register<JavaExec>("runClient") {
    group = "application"
    mainClass.set("uk.wwws.checkers.apps.entrypoints.launchers.ClientLauncher")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}

tasks.register<JavaExec>("runServer") {
    group = "application"
    mainClass.set("uk.wwws.checkers.apps.entrypoints.ServerApp")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}

tasks.register<JavaExec>("runAI") {
    group = "application"
    mainClass.set("uk.wwws.checkers.apps.entrypoints.launchers.AILauncher")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}

tasks.register<Jar>("jarClient") {
    group = "build"
    archiveBaseName.set("client")
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers." +
                "apps.entrypoints.launchers.ClientLauncher")
    }
    from(
        sourceSets["main"].runtimeClasspath,
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register<Jar>("jarAI") {
    group = "build"
    archiveBaseName.set("ai")
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers.apps.entrypoints.launchers.AILauncher")
    }
    from(
        sourceSets["main"].runtimeClasspath,
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register<Jar>("jarServer") {
    group = "build"
    archiveBaseName.set("server")
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers.apps.entrypoints.ServerApp")
    }
    from(
        sourceSets["main"].runtimeClasspath,
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register("jarAll") {
    group = "build"
    dependsOn("clean", "jarAI", "jarServer", "jarClient")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("org.apache.logging.log4j:log4j-api:2.22.0")
    implementation("org.apache.logging.log4j:log4j-core:2.22.0")

    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:win")
    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:linux")
    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:mac")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.aspectj:aspectjrt:1.9.25")
}

tasks.test {
    useJUnitPlatform()
}
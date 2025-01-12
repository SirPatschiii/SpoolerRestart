plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.javamodularity.moduleplugin") version "1.8.15"
}

group = "de.sirpatschiii"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-core:1.5.16")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.postgresql:postgresql:42.7.4")
}

application {
    mainClass.set("de.sirpatschiii.Main")
}

javafx {
    version = "21.0.5"
    modules(
        "javafx.base", "javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.media", "javafx.swing", "javafx.web"
    )
}

java {
    modularity.inferModulePath.set(false)
}

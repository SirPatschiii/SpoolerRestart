plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.beryx.jlink") version "3.1.1"
}

group = "de.sirpatschiii"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainModule.set("de.sirpatschiii")
    mainClass.set("de.sirpatschiii.Main")
}

javafx {
    version = "21.0.5"
    modules(
        "javafx.controls", "javafx.fxml"
    )
}

dependencies {
    // Logging
    implementation("ch.qos.logback:logback-core:1.5.16")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.slf4j:slf4j-api:2.0.16")

    // Java Native Access
    implementation("net.java.dev.jna:jna:5.16.0")
    implementation("net.java.dev.jna:jna-platform:5.16.0")

    // JavaFX
    implementation("org.openjfx:javafx-base:21.0.5")
    implementation("org.openjfx:javafx-controls:21.0.5")
    implementation("org.openjfx:javafx-fxml:21.0.5")
    implementation("org.openjfx:javafx-graphics:21.0.5")
    implementation("org.openjfx:javafx-media:21.0.5")
    implementation("org.openjfx:javafx-swing:21.0.5")
    implementation("org.openjfx:javafx-web:21.0.5")
}

jlink {
    options = listOf("--strip-debug", "--no-header-files", "--no-man-pages")
    launcher {
        name = "SpoolerRestart"
    }
    jpackage {
        installerOptions =
            listOf(
                "--win-shortcut",
                "--win-menu"
            )
        icon = "src/main/resources/icons/PrinterError.ico"
    }
}

package de.sirpatschiii.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum Configuration {
    INSTANCE;

    private final Logger logger;
    public final String HANDLE_URL = "https://download.sysinternals.com/files/Handle.zip";
    public final String HANDLE_EXE = "handle64.exe";
    public final String INSTALL_DIR = "C:\\Program Files\\SpoolerRestart\\tools";
    public final String PATH_HANDLE_EXE = Path.of(INSTALL_DIR).resolve(HANDLE_EXE).toString();

    Configuration() {
        logger = LoggerFactory.getLogger("ApplicationLogger");
    }

    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }

    public Logger getLogger() {
        return logger;
    }
}

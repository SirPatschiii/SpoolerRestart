package de.sirpatschiii.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Configuration {
    INSTANCE;

    private final Logger logger;
    public final String pathToHandleExe = "";

    Configuration() {
        logger = LoggerFactory.getLogger("ApplicationLogger");
    }

    public Logger getLogger() {
        return logger;
    }
}

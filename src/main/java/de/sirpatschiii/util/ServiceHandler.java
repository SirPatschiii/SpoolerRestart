package de.sirpatschiii.util;

import de.sirpatschiii.base.Configuration;
import org.slf4j.Logger;

public class ServiceHandler {
    private static final Logger logger = Configuration.INSTANCE.getLogger();

    public static boolean isServiceRunning(String service) {
        // If the result contains the keyword "RUNNING" the service is up
        return Shell.runCommandInBackground("sc query " + service).contains("RUNNING");
    }
}

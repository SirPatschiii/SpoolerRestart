package de.sirpatschiii;

import java.io.IOException;

public class ServiceHandler {
    public static boolean isServiceRunning(String service) {
        String result = "";

        // Get the result from the cmd command
        try {
            result = CMDHandler.executeCommandInvisible("sc query " + service);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // If the result contains the keyword "RUNNING" the service is up
        return result.contains("RUNNING");
    }
}

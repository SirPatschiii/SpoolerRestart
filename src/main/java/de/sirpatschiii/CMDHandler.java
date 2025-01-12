package de.sirpatschiii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CMDHandler {
    public static String executeCommandInvisible(String command) throws IOException, InterruptedException {
        // Create a ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.redirectErrorStream(true);

        // Start the process
        Process process = processBuilder.start();

        // Capture the output into a StringBuilder
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }

        // Wait for the process to complete
        process.waitFor();

        // Return the captured output as a String
        return sb.toString();
    }

    public static void executeCommandWithElevation(String command) throws IOException {
        // Construct the PowerShell command to run with elevation
        String powershellCommand = String.format("Start-Process cmd.exe -ArgumentList '/c %s' -Verb RunAs", command);

        // Use PowerShell to trigger the UAC prompt
        ProcessBuilder processBuilder = new ProcessBuilder("powershell", "-Command", powershellCommand);
        processBuilder.redirectErrorStream(true);

        // Start the process
        Process process = processBuilder.start();

        // Wait for the process to complete
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

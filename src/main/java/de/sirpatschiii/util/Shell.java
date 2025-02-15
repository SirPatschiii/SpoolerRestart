package de.sirpatschiii.util;

import com.sun.jna.platform.win32.*;
import de.sirpatschiii.base.Configuration;
import org.slf4j.Logger;

import java.io.*;

public class Shell {
    private static final Logger logger = Configuration.INSTANCE.getLogger();

    public static String runCommandInBackground(String command) {
        // Generate a temp file to return output
        File tempFile;
        try {
            tempFile = File.createTempFile("SpoolerRestartTemp", ".txt");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            logger.error("In the temp file creation process occurred an error!", e);
            throw new RuntimeException(e);
        }

        // Setup ShellExecuteEx structure
        ShellAPI.SHELLEXECUTEINFO execInfo = new ShellAPI.SHELLEXECUTEINFO();
        execInfo.cbSize = execInfo.size();
        execInfo.fMask = 0x00000040;
        execInfo.lpFile = "cmd.exe";
        execInfo.lpParameters = "/c " + command + " > " + tempFile.getAbsolutePath();
        execInfo.nShow = WinUser.SW_HIDE;

        logger.info(execInfo.lpParameters);

        // Execute the command
        if (!Shell32.INSTANCE.ShellExecuteEx(execInfo)) {
            logger.error("In the execution process of the command \"{}\" occurred an error!", execInfo.lpParameters);
            throw new RuntimeException("ShellExecuteEx failed with the following command: " + execInfo.lpParameters);
        }

        // Wait for the process to complete
        if (execInfo.hProcess != null) {
            int result = Kernel32.INSTANCE.WaitForSingleObject(execInfo.hProcess, WinBase.INFINITE);
            if (result == WinBase.WAIT_OBJECT_0) {
                Kernel32.INSTANCE.CloseHandle(execInfo.hProcess);
            } else if (result == WinBase.WAIT_FAILED) {
                logger.error("An handle didn't come to a stop!");
                throw new RuntimeException("An handle didn't come to a stop!");
            }
        }

        // Read output
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            reader = new BufferedReader(new FileReader(tempFile));
        } catch (FileNotFoundException e) {
            logger.error("A file reader couldn't read a temp file created by a shell!", e);
            throw new RuntimeException(e);
        }

        try {
            line = reader.readLine();
        } catch (IOException e) {
            logger.error("A text line couldn't been read from a temp file!", e);
            throw new RuntimeException(e);
        }

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            try {
                line = reader.readLine();
            } catch (IOException e) {
                logger.error("A text line couldn't been read from a temp file!", e);
                throw new RuntimeException(e);
            }
        }

        return sb.toString();
    }

    public static String runCommandInBackgroundPP(String command) {
        // Generate a temp file to return output
        File tempFile;
        try {
            tempFile = File.createTempFile("SpoolerRestartTemp", ".txt");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            logger.error("In the temp file creation process occurred an error!", e);
            throw new RuntimeException(e);
        }

        // Build the command
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command + " > " + tempFile.getAbsolutePath());
        processBuilder.redirectErrorStream(true);

        // Execute the command
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor(); // Wait for the process to finish
            if (exitCode != 0) {
                logger.error("Command execution failed with exit code {}", exitCode);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("An error occurred executing a command!", e);
            throw new RuntimeException(e);
        }

        // Read output
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            reader = new BufferedReader(new FileReader(tempFile));
        } catch (FileNotFoundException e) {
            logger.error("A file reader couldn't read a temp file created by a shell!", e);
            throw new RuntimeException(e);
        }

        try {
            line = reader.readLine();
        } catch (IOException e) {
            logger.error("A text line couldn't been read from a temp file!", e);
            throw new RuntimeException(e);
        }

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            try {
                line = reader.readLine();
            } catch (IOException e) {
                logger.error("A text line couldn't been read from a temp file!", e);
                throw new RuntimeException(e);
            }
        }

        return sb.toString();
    }

    public static boolean isCurrentProcessElevated() {
        return Advapi32Util.isCurrentProcessElevated();
    }
}

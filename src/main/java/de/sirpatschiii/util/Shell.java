package de.sirpatschiii.util;

import com.sun.jna.platform.win32.*;
import de.sirpatschiii.alerts.errorhandler.ErrorBus;
import de.sirpatschiii.base.Configuration;
import org.slf4j.Logger;

import java.io.*;

public class Shell {
    private static final Logger logger = Configuration.INSTANCE.getLogger();

    public static String runCommandInBackground(String command) {
        // Generate a temp file to return output
        File tempFile = null;
        try {
            tempFile = File.createTempFile("SpoolerRestartTemp", ".txt");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            ErrorBus.getInstance().reportError("In the temp file creation process occurred an error!", e);
            System.exit(-1);
        }

        // Setup ShellExecuteEx structure
        ShellAPI.SHELLEXECUTEINFO execInfo = new ShellAPI.SHELLEXECUTEINFO();
        execInfo.cbSize = execInfo.size();
        execInfo.fMask = 0x00000040;
        execInfo.lpFile = "cmd.exe";
        execInfo.lpParameters = "/c " + command + " > " + tempFile.getAbsolutePath();
        execInfo.nShow = WinUser.SW_HIDE;

        logger.info("Ran ShellExecute command: {}", execInfo.lpParameters);

        // Execute the command
        if (!Shell32.INSTANCE.ShellExecuteEx(execInfo)) {
            ErrorBus.getInstance().reportError(new ShellException("In the execution process of the command \"" + execInfo.lpParameters + "\" occurred an error!", null));
        }

        // Wait for the process to complete
        if (execInfo.hProcess != null) {
            int result = Kernel32.INSTANCE.WaitForSingleObject(execInfo.hProcess, WinBase.INFINITE);
            if (result == WinBase.WAIT_OBJECT_0) {
                Kernel32.INSTANCE.CloseHandle(execInfo.hProcess);
            } else if (result == WinBase.WAIT_FAILED) {
                ErrorBus.getInstance().reportError(new ShellException("An handle didn't come to a stop!", null));
            }
        }

        // Read output
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            reader = new BufferedReader(new FileReader(tempFile));
        } catch (FileNotFoundException e) {
            ErrorBus.getInstance().reportError("A file reader couldn't read a temp file created by a shell!", e);
            System.exit(-1);
        }

        try {
            line = reader.readLine();
        } catch (IOException e) {
            ErrorBus.getInstance().reportError("A text line couldn't been read from a temp file!", e);
        }

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            try {
                line = reader.readLine();
            } catch (IOException e) {
                ErrorBus.getInstance().reportError("A text line couldn't been read from a temp file!", e);
            }
        }

        return sb.toString();
    }

    public static String runCommandInBackgroundPP(String command) {
        // Generate a temp file to return output
        File tempFile = null;
        try {
            tempFile = File.createTempFile("SpoolerRestartTemp", ".txt");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            ErrorBus.getInstance().reportError("In the temp file creation process occurred an error!", e);
            System.exit(-1);
        }

        // Build the command
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command + " > " + tempFile.getAbsolutePath());
        processBuilder.redirectErrorStream(true);

        // Execute the command
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor(); // Wait for the process to finish
            if (exitCode != 0) {
                ErrorBus.getInstance().reportError(new ShellException("Command execution failed with exit code " + exitCode, null));
            }
        } catch (IOException | InterruptedException e) {
            ErrorBus.getInstance().reportError(new ShellException("An error occurred executing a command!", e));
        }

        // Read output
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            reader = new BufferedReader(new FileReader(tempFile));
        } catch (FileNotFoundException e) {
            ErrorBus.getInstance().reportError("A file reader couldn't read a temp file created by a shell!", e);
            System.exit(-1);
        }

        try {
            line = reader.readLine();
        } catch (IOException e) {
            ErrorBus.getInstance().reportError("A text line couldn't been read from a temp file!", e);
        }

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            try {
                line = reader.readLine();
            } catch (IOException e) {
                ErrorBus.getInstance().reportError("A text line couldn't been read from a temp file!", e);
            }
        }

        return sb.toString();
    }

    public static boolean isCurrentProcessElevated() {
        return Advapi32Util.isCurrentProcessElevated();
    }
}

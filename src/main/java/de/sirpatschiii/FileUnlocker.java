package de.sirpatschiii;

import de.sirpatschiii.base.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUnlocker {
    public static void unlockFiles(ArrayList<File> files) {
        String resultHandleEXE = FileLockChecker.resultHandleEXE;
        String filePath;
        int pid;
        String handleID;

        // Split result to check all processes
        List<String> sections = Arrays.stream(resultHandleEXE.split("------------------------------------------------------------------------------")).toList();

        // Collect pid which is blocking the file
        for (File file : files) {
            // Get filename to search for
            filePath = file.getAbsolutePath();

            // search the sections (one section equals one process) for the file
            for (String section : sections) {
                if (section.contains(filePath)) {
                    pid = -1;
                    handleID = "";
                    // Split the found section to find the pid successfully
                    String[] subsections = section.split(" ");
                    for (int i = 0; i < subsections.length; i++) {
                        if (subsections[i].equals("pid:")) {
                            pid = Integer.parseInt(subsections[i + 1]);
                            break;
                        }
                    }
                    // Split the found section to find the handleID successfully
                    String[] lines = section.split("\n");
                    for (String line : lines) {
                        if (line.contains(filePath)) {
                            String[] lineParts = line.split(" ");
                            for (String linePart : lineParts) {
                                if (!linePart.isEmpty()) {
                                    handleID = linePart.substring(0, linePart.length() - 1);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    releaseHandle(pid, handleID);
                    System.out.println(pid + " " + handleID);
                }
            }
        }
    }

    private static void releaseHandle(int pid, String handleID) {
        // Return if pid is illegal
        if (pid == -1 || handleID.isEmpty()) {
            return;
        }

        // Kill the process by its pid
        String killCommand = Configuration.INSTANCE.pathToHandleExe + " -c " + handleID + " -y -p " + pid + " -accepteula";
        try {
            CMDHandler.executeCommandWithElevation(killCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

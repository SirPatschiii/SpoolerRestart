package de.sirpatschiii.handle64;

import de.sirpatschiii.base.Configuration;
import de.sirpatschiii.util.Shell;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Handle64Manager {
    private static final Logger logger = Configuration.INSTANCE.getLogger();
    private static final String PATH_HANDLE_EXE = Configuration.INSTANCE.PATH_HANDLE_EXE;

    public static void unlockFiles(ArrayList<File> files) {
        ArrayList<String> nonEmptyParts = new ArrayList<>();
        String result;
        String handleID;
        int pid;

        // Iterate over all files and try to unlock even if they aren't locked
        for (File file : files) {
            // Check if there is an entry for the current file
            result = Shell.runCommandInBackgroundPP("\"" + PATH_HANDLE_EXE + "\" \"" + file.getAbsolutePath() + "\" -accepteula");
            if (!result.contains("No matching handles found.") && !result.isEmpty()) {
                List<String> parts = Arrays.stream(result.split(" ")).toList();
                // Get rid of all empty parts
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        nonEmptyParts.addLast(part);
                    }
                }
                // Determine pid and handleID
                pid = -1;
                handleID = "";
                int c = 0;
                for (String part : nonEmptyParts) {
                    if (part.contains("pid:")) {
                        pid = Integer.parseInt(nonEmptyParts.get(c + 1));
                    }
                    if (part.contains("type:")) {
                        handleID = nonEmptyParts.get(c + 2).replace(":", "");
                    }
                    c++;
                }
                // Release the handle
                releaseHandle(pid, handleID);
            }
        }
    }

    private static void releaseHandle(int pid, String handleID) {
        // Return if pid is illegal
        if (pid == -1 || handleID.isEmpty()) {
            return;
        }

        // Get rid of the lock by its pid and handleID
        Shell.runCommandInBackground("\"" + Configuration.INSTANCE.PATH_HANDLE_EXE + "\" -c " + handleID + " -y -p " + pid + " -accepteula");
        logger.info("Removed handle {} from process {}.", handleID, pid);
    }
}

package de.sirpatschiii;

import de.sirpatschiii.base.Configuration;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileLockChecker {
    private static final Logger logger = Configuration.INSTANCE.getLogger();
    public static String resultHandleEXE = "";

    public static ArrayList<File> collectLockedFiles(ArrayList<File> files) {
        ArrayList<File> lockedFiles = new ArrayList<>();
        String filename;

        // Get all locked files from handle.exe
        try {
            String command = Configuration.INSTANCE.pathToHandleExe + " -accepteula";
            resultHandleEXE = CMDHandler.executeCommandInvisible(command);
        } catch (IOException | InterruptedException e) {
            logger.error("In the process of collecting all locked files via handle.exe occurred an error!", e);
            return null;
        }

        // Determine locked files
        for (File file : files) {
            filename = file.getName();
            if (resultHandleEXE.contains(filename)) {
                lockedFiles.addLast(file);
            }
        }

        return lockedFiles;
    }
}

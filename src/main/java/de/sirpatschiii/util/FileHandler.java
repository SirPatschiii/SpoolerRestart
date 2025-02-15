package de.sirpatschiii.util;

import java.io.File;
import java.util.ArrayList;

public class FileHandler {
    public static ArrayList<File> getAllFilesFromSpoolerQueue() {
        ArrayList<File> files = new ArrayList<>();
        ArrayList<File> directories = new ArrayList<>();

        // Add the default directory
        directories.add(new File(System.getenv("SystemRoot") + "\\System32\\spool\\PRINTERS"));

        // Collect subdirectories
        collectSubdirectories(directories);

        // Collect all files
        collectFiles(files, directories);

        return files;
    }

    public static ArrayList<File> getAllDirectoriesFromSpoolerQueue() {
        ArrayList<File> directories = new ArrayList<>();

        // Add the default directory
        directories.add(new File(System.getenv("SystemRoot") + "\\System32\\spool\\PRINTERS"));

        // Collect subdirectories
        collectSubdirectories(directories);

        // Remove the default directory
        directories.remove(new File(System.getenv("SystemRoot") + "\\System32\\spool\\PRINTERS"));

        return directories;
    }

    public static boolean deleteFile(File file) {
        boolean success = false;

        // Delete the file if it exists
        if (file.exists() && file.isFile()) {
            success = file.delete();
        }

        return success;
    }

    public static int deleteFiles(ArrayList<File> files) {
        boolean success;
        int c = 0;

        // Delete all files according to the file list
        for (File file : files) {
            success = false;
            // Try to delete the file if it exists
            if (file.exists() && file.isFile()) {
                success = file.delete();
            }
            // Increment counter if deletion was successful
            if (success) {
                c++;
            }
        }

        return c;
    }

    public static int deleteDirectories(ArrayList<File> directories) {
        boolean success;
        int c = 0;

        // Delete all directories according to the directory list
        for (File directory : directories) {
            success = false;
            // Try to delete the directory if it exists
            if (directory.exists() && directory.isDirectory()) {
                success = directory.delete();
            }
            // Increment counter if deletion was successful
            if (success) {
                c++;
            }
        }

        return c;
    }

    private static void collectSubdirectories(ArrayList<File> directories) {
        // Save all directories to another ArrayList
        ArrayList<File> directoriesToProcess = new ArrayList<>();
        for (File file : directories) {
            if (file.exists() && file.isDirectory()) {
                directoriesToProcess.addLast(file);
            }
        }

        // While there are directories to process
        while (!directoriesToProcess.isEmpty()) {
            // Get the current directory to process
            File currentDir = directoriesToProcess.removeFirst();

            // Check if the directory exists and is actually a directory
            if (currentDir.exists() && currentDir.isDirectory()) {
                // Get all the files and subdirectories in the current directory
                File[] filesInDir = currentDir.listFiles();

                if (filesInDir != null) {
                    for (File file : filesInDir) {
                        // Only add subdirectories to the list (skip files)
                        if (file.isDirectory() && !file.getName().equals(".") && !file.getName().equals("..")) {
                            directoriesToProcess.addLast(file);
                            directories.addFirst(file);
                        }
                    }
                }
            }
        }
    }

    private static void collectFiles(ArrayList<File> files, ArrayList<File> directories) {
        // While there are directories to process
        while (!directories.isEmpty()) {
            // Get the first directory to process
            File currentDir = directories.removeFirst();

            // Check if the directory exists and is actually a directory
            if (currentDir.exists() && currentDir.isDirectory()) {
                // Get all the files and subdirectories in the current directory
                File[] filesInDir = currentDir.listFiles();

                if (filesInDir != null) {
                    for (File file : filesInDir) {
                        // Only add files to the list
                        if (file.exists() && file.isFile()) {
                            files.addLast(file);
                        }
                    }
                }
            }
        }
    }
}

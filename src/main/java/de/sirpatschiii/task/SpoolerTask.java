package de.sirpatschiii.task;

import de.sirpatschiii.base.Configuration;
import de.sirpatschiii.gui.GUIController;
import de.sirpatschiii.handle64.Handle64Manager;
import de.sirpatschiii.util.FileHandler;
import de.sirpatschiii.util.ServiceHandler;
import de.sirpatschiii.util.Shell;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;

public class SpoolerTask extends Task<Void> {
    private static final Logger logger = Configuration.INSTANCE.getLogger();

    private final boolean clearFiles;
    private final boolean forceClearFiles;
    private final GUIController controller;

    public SpoolerTask(boolean clearFiles, boolean forceClearFiles, GUIController controller) {
        this.clearFiles = clearFiles;
        this.forceClearFiles = forceClearFiles;
        this.controller = controller;
    }

    @Override
    protected Void call() throws Exception {
        try {
            ArrayList<File> files;
            ArrayList<File> directories;
            ArrayList<File> lockedFiles;
            boolean spoolerRunning;

            // Determine spooler status
            spoolerRunning = isServiceRunning();

            // Stop service if running
            if (spoolerRunning) {
                stopSpooler();
            }

            // Only proceed if files should be deleted
            if (clearFiles) {
                // Determine all files in the current spooler queue
                files = getAllFilesFromSpoolerQueue();

                // Determine all subdirectories in the current spooler queue
                directories = getAllDirectoriesFromSpoolerQueue();

                // Only proceed if deletion should be forced and there are files existent
                if (forceClearFiles && !files.isEmpty()) {
                    // Unlock all files
                    unlockFiles(files);
                }

                // Delete all files
                deleteFiles(files);

                // Delete all directories
                deleteDirectories(directories);
            }

            // Start spooler service
            startSpooler();

            // Wait before terminating to complete console output
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error("In the execution of the processing background thread occurred an error!", e);
            controller.appendToConsole("Fehler: " + e.getMessage());
        }
        return null;
    }

    private boolean isServiceRunning() {
        out("Überprüfe Status der Druckwarteschlange...\n");
        return ServiceHandler.isServiceRunning("Spooler");
    }

    private void stopSpooler() throws InterruptedException {
        out("Druckwarteschlange wird beendet.");
        Shell.runCommandInBackground("net stop spooler");
        Thread.sleep(500);
        out("Druckwarteschlange erfolgreich beendet.\n");
    }

    private void startSpooler() throws InterruptedException {
        out("Druckwarteschlange wird gestartet.");
        Shell.runCommandInBackground("net start spooler");
        Thread.sleep(500);
        out("Druckwarteschlange erfolgreich gestartet.\n");
    }

    private ArrayList<File> getAllFilesFromSpoolerQueue() {
        ArrayList<File> files = FileHandler.getAllFilesFromSpoolerQueue();
        if (files.isEmpty()) {
            out("Keine Dateien in der Druckerwarteschlange gefunden.");
        } else if (files.size() == 1) {
            out(files.size() + " Datei in der Druckerwarteschlange gefunden.");
        } else {
            out(files.size() + " Dateien in der Druckerwarteschlange gefunden.");
        }
        return files;
    }

    private ArrayList<File> getAllDirectoriesFromSpoolerQueue() {
        ArrayList<File> directories = FileHandler.getAllDirectoriesFromSpoolerQueue();
        if (directories.isEmpty()) {
            out("Keine Verzeichnisse in der Druckerwarteschlange gefunden.");
        } else if (directories.size() == 1) {
            out(directories.size() + " Verzeichnis in der Druckerwarteschlange gefunden.");
        } else {
            out(directories.size() + " Verzeichnisse in der Druckerwarteschlange gefunden.");
        }
        return directories;
    }

    private void unlockFiles(ArrayList<File> files) {
        Handle64Manager.unlockFiles(files);
        out("Blockierte Dateien freigegeben.");
    }

    private void deleteFiles(ArrayList<File> files) {
        int amountFiles = FileHandler.deleteFiles(files);
        if (amountFiles == 1) {
            out(amountFiles + " Datei wurde gelöscht.");
        } else {
            out(amountFiles + " Dateien wurden gelöscht.");
        }
    }

    private void deleteDirectories(ArrayList<File> directories) {
        int amountDirs = FileHandler.deleteDirectories(directories);
        if (amountDirs == 1) {
            out(amountDirs + " Verzeichnis wurde gelöscht.\n");
        } else {
            out(amountDirs + " Verzeichnisse wurden gelöscht.\n");
        }
    }

    private void out(String message) {
        Platform.runLater(() -> controller.appendToConsole(message));
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        controller.activateBTNStart();
        out("Vorgang erfolgreich abgeschlossen!");
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        controller.activateBTNStart();
        out("Vorgang abgebrochen!");
    }

    @Override
    protected void failed() {
        super.failed();
        controller.activateBTNStart();
        out("Vorgang fehlgeschlagen!");
    }
}

package de.sirpatschiii.gui;

import de.sirpatschiii.*;
import de.sirpatschiii.alerts.WarningAlert;
import de.sirpatschiii.base.BaseWindowController;
import de.sirpatschiii.base.Configuration;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class GUIController extends BaseWindowController {
    @FXML
    TextArea txaConsole;
    @FXML
    CheckBox chkClearFiles;
    @FXML
    CheckBox chkForceClearFiles;
    @FXML
    Button btnStart;

    private static final Logger logger = Configuration.INSTANCE.getLogger();

    @FXML
    public void initialize() {
        chkForceClearFiles.setDisable(true);

        String message = """
                Achtung!
                Diese Option entzieht Prozessen, welche ggf. Dateien in der Druckerwarteschlange sperren, den sogenannten Lock.
                Dieser verhindert eigentlich das Löschen sofern ein anderer Prozess schon auf die Datei zugreift.
                Wünschen Sie trotzdem die Dateien zu löschen, dann können Sie dies mit dieser Option erzwingen.
                Wähle Sie die Option nur aus, sofern Sie sich der Risiken bewusst sind!""";

        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setShowDuration(Duration.seconds(15));
        chkForceClearFiles.setTooltip(tooltip);
    }

    public void handleActionOnCHKClearFiles(ActionEvent event) {
        // Only activate force clear if default clear is set
        if (chkClearFiles.isSelected()) {
            chkForceClearFiles.setDisable(false);
        } else {
            chkForceClearFiles.setDisable(true);
            chkForceClearFiles.setSelected(false);
        }
    }

    public void handleActionOnBTNStart(ActionEvent event) {
        boolean clearFiles = chkClearFiles.isSelected();
        boolean forceClearFiles = chkForceClearFiles.isSelected();

        // Open dialog to confirm the user input
        WarningAlert alert = new WarningAlert();
        alert.setHeader("Vorgang starten?");
        alert.setMessage("Sind Sie sicher, dass Sie den Prozess starten möchten?");
        if (forceClearFiles) {
            alert.setMessage("Sind Sie sicher, dass Sie den Prozess starten möchten?");
            alert.addMessage("");
            alert.addMessage("Achtung, Sie erzwingen das Löschen der Dateien,");
            alert.addMessage("welche sich in der Druckerwarteschlange befinden!");
        }
        alert.setBTNAcceptText("Start");
        alert.showAndWait();

        // Get the dialog result and start the process if dialog accepted
        ButtonType result = alert.getResult();
        if (result.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            // Disable the start button and clear console
            btnStart.setDisable(true);
            txaConsole.setText("");

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        ArrayList<File> files;
                        ArrayList<File> directories;
                        ArrayList<File> lockedFiles;
                        boolean spoolerRunning;

                        // Determine spooler status
                        Platform.runLater(() -> {
                            appendToConsole("Überprüfe Status der Druckerwarteschlange...");
                            appendToConsole("");
                        });
                        spoolerRunning = ServiceHandler.isServiceRunning("Spooler");

                        // Stop service if running
                        if (spoolerRunning) {
                            Platform.runLater(() -> appendToConsole("Beende Druckerwarteschlange..."));
                            CMDHandler.executeCommandWithElevation("net stop spooler");
                            Thread.sleep(5000);
                            Platform.runLater(() -> {
                                appendToConsole("Druckerwarteschlange erfolgreich beendet.");
                                appendToConsole("");
                            });
                        }

                        // Only proceed if files should be deleted
                        if (clearFiles) {
                            // Determine all files in the current spooler queue
                            files = FileHandler.getAllFilesFromSpoolerQueue();
                            Platform.runLater(() -> {
                                if (files.isEmpty()) {
                                    appendToConsole("Keine Dateien in der Druckerwarteschlange gefunden.");
                                } else if (files.size() == 1) {
                                    appendToConsole(files.size() + " Datei in der Druckerwarteschlange gefunden.");
                                } else {
                                    appendToConsole(files.size() + " Dateien in der Druckerwarteschlange gefunden.");
                                }
                            });
                            Thread.sleep(200);

                            // Determine all subdirectories in the current spooler queue
                            directories = FileHandler.getAllDirectoriesFromSpoolerQueue();
                            Platform.runLater(() -> {
                                if (directories.isEmpty()) {
                                    appendToConsole("Keine Verzeichnisse in der Druckerwarteschlange gefunden.");
                                } else if (directories.size() == 1) {
                                    appendToConsole(directories.size() + " Verzeichnis in der Druckerwarteschlange gefunden.");
                                } else {
                                    appendToConsole(directories.size() + " Verzeichnisse in der Druckerwarteschlange gefunden.");
                                }
                            });
                            Thread.sleep(200);

                            // Only proceed if deletion should be forced and there are files existent
                            if (forceClearFiles && !files.isEmpty()) {
                                // Collect all locked files
                                lockedFiles = FileLockChecker.collectLockedFiles(files);
                                Platform.runLater(() -> {
                                    if (lockedFiles == null || lockedFiles.isEmpty()) {
                                        appendToConsole("");
                                        appendToConsole("Keine blockierten Dateien aufgefunden.");
                                        appendToConsole("");
                                    } else if (lockedFiles.size() == 1) {
                                        appendToConsole("");
                                        appendToConsole(lockedFiles.size() + " blockierte Datei wurde gefunden.");
                                    } else {
                                        appendToConsole("");
                                        appendToConsole(lockedFiles.size() + " blockierte Dateien wurden gefunden.");
                                    }
                                });
                                Thread.sleep(200);

                                // Unlock all locked files
                                if (lockedFiles != null && !lockedFiles.isEmpty()) {
                                    FileUnlocker.unlockFiles(lockedFiles);
                                    Platform.runLater(() -> {
                                        appendToConsole("Blockierte Dateien freigegeben.");
                                        appendToConsole("");
                                    });
                                    Thread.sleep(200);
                                }
                            }

                            // Delete all files
                            int amountFiles = FileHandler.deleteFiles(files);
                            Platform.runLater(() -> {
                                if (amountFiles == 1) {
                                    appendToConsole(amountFiles + " Datei wurde gelöscht.");
                                } else {
                                    appendToConsole(amountFiles + " Dateien wurden gelöscht.");
                                }
                            });
                            Thread.sleep(200);

                            // Delete all directories
                            int amountDirs = FileHandler.deleteDirectories(directories);
                            Platform.runLater(() -> {
                                if (amountDirs == 1) {
                                    appendToConsole(amountDirs + " Verzeichnis wurde gelöscht.");
                                } else {
                                    appendToConsole(amountDirs + " Verzeichnisse wurden gelöscht.");
                                }
                                appendToConsole("");
                            });
                            Thread.sleep(200);
                        }

                        // Start spooler service
                        Platform.runLater(() -> appendToConsole("Starte Druckerwarteschlange..."));
                        CMDHandler.executeCommandWithElevation("net start spooler");
                        Thread.sleep(5000);
                        Platform.runLater(() -> {
                            appendToConsole("Druckerwarteschlange erfolgreich gestartet.");
                            appendToConsole("");
                        });

                    } catch (IOException | InterruptedException e) {
                        logger.error("In the execution of the processing background thread occurred an error!", e);
                        appendToConsole("Fehler: " + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    btnStart.setDisable(false);
                    Platform.runLater(() -> appendToConsole("Vorgang erfolgreich abgeschlossen!"));
                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    btnStart.setDisable(false);
                    Platform.runLater(() -> appendToConsole("Vorgang abgebrochen!"));
                }

                @Override
                protected void failed() {
                    super.failed();
                    btnStart.setDisable(false);
                    Platform.runLater(() -> appendToConsole("Vorgang fehlgeschlagen!"));
                }
            };

            // Start the task on a new thread
            new Thread(task).start();
        }
    }

    private void appendToConsole(String message) {
        // Append messages to the console and log them further scroll console to the end
        Platform.runLater(() -> {
            txaConsole.appendText(message + "\n");
            logger.info(message);
            txaConsole.setScrollTop(Double.MAX_VALUE);
        });
    }
}

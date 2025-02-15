package de.sirpatschiii.gui;

import de.sirpatschiii.alerts.InfoAlert;
import de.sirpatschiii.alerts.WarningAlert;
import de.sirpatschiii.base.BaseWindowController;
import de.sirpatschiii.base.Configuration;
import de.sirpatschiii.handle64.Handle64Downloader;
import de.sirpatschiii.task.SpoolerTask;
import de.sirpatschiii.util.Shell;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.slf4j.Logger;

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
        // Disable force clear option
        chkForceClearFiles.setDisable(true);

        // Add tooltip to the force clear option
        chkForceClearFiles.setTooltip(getForceClearTooltip());

        // Check if application is admin and warn the user if it isn't
        showRequiredAdminHintIfNecessary();
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

        if (promptIfProcessShouldBeStarted(forceClearFiles)) {
            // Disable the start button and clear console
            btnStart.setDisable(true);
            txaConsole.setText("");

            // Check if handle.exe is installed
            if (forceClearFiles) {
                if (!installHandleExeIfNecessary()) {
                    appendToConsole("Abbruch, handle.exe benötigt!");
                    btnStart.setDisable(false);
                    return;
                }
            }

            // Start the background process
            manageBackgroundTask(clearFiles, forceClearFiles);
        }
    }

    private Tooltip getForceClearTooltip() {
        String message = """
                Achtung!
                Diese Option entzieht Prozessen, welche ggf. Dateien in der Druckerwarteschlange sperren, den sogenannten Lock.
                Dieser verhindert eigentlich das Löschen sofern ein anderer Prozess schon auf die Datei zugreift.
                Wünschen Sie trotzdem die Dateien zu löschen, dann können Sie dies mit dieser Option erzwingen.
                Wähle Sie die Option nur aus, sofern Sie sich der Risiken bewusst sind!""";

        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setShowDuration(Duration.seconds(15));
        return tooltip;
    }

    private void showRequiredAdminHintIfNecessary() {
        if (!Shell.isCurrentProcessElevated()) {
            // Deactivate start button
            btnStart.setDisable(true);

            // Show warning after the stage has been build up
            Platform.runLater(() -> {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Show warning alert that admin rights are required
                InfoAlert alert = new InfoAlert();
                alert.setHeader("Adminrechte benötigt!");
                alert.setMessage("Das Programm wird aktuell ohne Adminrechte ausgeführt. Administratorrechte werden benötigt, da der Windowsdienst 'Druckerwarteschlange' sonst nicht bearbeitet werden kann. Bitte starten Sie das Programm als Administrator neu.");
                alert.setBTNAcceptText("Akzeptieren");
                alert.showAndWait();
            });
        }
    }

    private boolean promptIfProcessShouldBeStarted(boolean forceClearFiles) {
        WarningAlert alert = new WarningAlert();
        alert.setHeader("Vorgang starten?");
        alert.setMessage("Sind Sie sicher, dass Sie den Prozess starten möchten?");
        if (forceClearFiles) {
            alert.setMessage("Sind Sie sicher, dass Sie den Prozess starten möchten? \n Achtung, Sie erzwingen das Löschen der Dateien, welche sich in der Druckerwarteschlange befinden!");
        }
        alert.setBTNAcceptText("Start");
        alert.setBTNCancelText("Abbrechen");
        alert.showAndWait();

        return alert.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    private boolean installHandleExeIfNecessary() {
        if (!Handle64Downloader.isHandle64Existent()) {
            WarningAlert alert = new WarningAlert();
            alert.setHeader("Handle64.exe benötigt");
            alert.setMessage("Zum Ausführen des Prozesses wird handle64.exe von der Microsoft Sysinternals Suite benötigt. Mit dem Bestätigen des Downloads akzeptieren Sie die geltenden Lizenzbedingungen!");
            alert.setBTNAcceptText("Download");
            alert.setBTNCancelText("Abbrechen");
            alert.showAndWait();

            if (alert.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Handle64Downloader.downloadAndExtractHandle64();
                appendToConsole("Handle.exe wurde erfolgreich heruntergeladen!");
                return true;
            }
            return false;
        }
        return true;
    }

    private void manageBackgroundTask(boolean clearFiles, boolean forceClearFiles) {
        SpoolerTask task = new SpoolerTask(clearFiles, forceClearFiles, this);

        // Start the task on a new thread
        new Thread(task).start();
    }

    public void appendToConsole(String message) {
        // Append messages to the console and log them further scroll console to the end
        Platform.runLater(() -> {
            txaConsole.appendText(message + "\n");
            txaConsole.setScrollTop(Double.MAX_VALUE);
            logger.info(message);
        });
    }

    public void activateBTNStart() {
        btnStart.setDisable(false);
    }
}

package de.sirpatschiii.alerts.errorhandler;

import de.sirpatschiii.alerts.InfoAlert;
import de.sirpatschiii.base.BaseWindowController;
import de.sirpatschiii.base.Configuration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ErrorHandlerController extends BaseWindowController {
    @FXML
    Label lblException;
    @FXML
    TextArea txaStacktrace;
    @FXML
    Button btnSave;
    @FXML
    Button btnCopy;
    @FXML
    Button btnIgnore;
    @FXML
    Button btnEnd;

    @Override
    public void handleActionOnBTNExit(ActionEvent event) {
        System.exit(-1);
    }

    public void handleOnMouseReleasedOnIMGHelp(MouseEvent event) {
        InfoAlert alert = new InfoAlert();
        alert.setHeader("Help");
        alert.setMessage("You currently see the ErrorHandler. This occurs if an unexpected problem has occurred. You can see some details about the error on this screen. If the error persists, you can save the error and contact support for help.");
        alert.setBTNAcceptText("Okay");
        alert.showAndWait();
    }

    public void handleActionOnBTNSave(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Generate a suggested filename
        String timestamp = Configuration.INSTANCE.getCurrentTime();
        String suggestedFileName = "errorHandler_" + timestamp + ".txt";

        // Init save dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName(suggestedFileName);

        // Show save dialog
        File file = fileChooser.showSaveDialog(stage);

        // Save the file if existent
        if (file != null) {
            try (BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                bw.write(txaStacktrace.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleActionOnBTNCopy(ActionEvent event) {
        // Grab the system clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();

        // Create a new entry
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(txaStacktrace.getText());

        // Add the entry to the clipboard
        clipboard.setContent(clipboardContent);
    }

    public void handleActionOnBTNIgnore(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void handleActionOnBTNEnd(ActionEvent event) {
        System.exit(-1);
    }

    public void setMessage(String message) {
        lblException.setText(message);
    }

    public void setStacktrace(String stacktrace) {
        txaStacktrace.setText(stacktrace);
    }
}

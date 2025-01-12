package de.sirpatschiii.alerts;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class WarningAlert extends CustomAlert {
    private WarningAlertController warningAlertController;

    public WarningAlert() {
        super();

        try {
            // Init alert stage
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/alerts/warningAlert.fxml")));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage = new Stage();

            // Set stage settings
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.centerOnScreen();

            // Store the controller to access later on
            warningAlertController = loader.getController();
        } catch (IOException e) {
            logger.error("An error occurred while switching a scene!", e);
        }
    }

    public void setBTNAcceptText(String text) {
        warningAlertController.setBTNAcceptText(text);
    }

    public void setBTNCancelText(String text) {
        warningAlertController.setBTNCancelText(text);
    }

    @Override
    public void setHeader(String title) {
        warningAlertController.setHeader(title);
        stage.setTitle(title);
    }

    @Override
    public void setMessage(String message) {
        warningAlertController.setMessage(message);
    }

    @Override
    public void addMessage(String message) {
        warningAlertController.addMessage(message);
    }

    @Override
    public ButtonType getResult() {
        return warningAlertController.getResult();
    }
}

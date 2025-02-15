package de.sirpatschiii.alerts;

import de.sirpatschiii.alerts.errorhandler.ErrorBus;
import de.sirpatschiii.base.SceneLoadException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class InfoAlert extends CustomAlert {
    private InfoAlertController infoAlertController;

    public InfoAlert() {
        super();

        try {
            // Init alert stage
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/alerts/infoAlert.fxml")));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage = new Stage();

            // Set stage settings
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinHeight(150);
            stage.setMinWidth(300);
            stage.setResizable(false);
            stage.centerOnScreen();

            // Store the controller to access later on
            infoAlertController = loader.getController();
        } catch (IOException e) {
            ErrorBus.getInstance().reportError(new SceneLoadException(e));
        }
    }

    public void setBTNAcceptText(String text) {
        infoAlertController.setBTNAcceptText(text);
    }

    @Override
    public void setHeader(String title) {
        infoAlertController.setHeader(title);
        stage.setTitle(title);
    }

    @Override
    public void setMessage(String message) {
        Dimension2D dimension2D = getWrapped16by9Dimensions(message);
        stage.setMinHeight(dimension2D.getHeight() + 112);
        stage.setMinWidth(dimension2D.getWidth() + 60);
        infoAlertController.setMessage(message);
    }

    @Override
    public ButtonType getResult() {
        return infoAlertController.getResult();
    }
}

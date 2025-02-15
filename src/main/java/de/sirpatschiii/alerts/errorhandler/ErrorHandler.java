package de.sirpatschiii.alerts.errorhandler;

import de.sirpatschiii.base.Configuration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class ErrorHandler {
    private static final Logger logger = Configuration.INSTANCE.getLogger();
    private ErrorHandlerController errorHandlerController;
    private Stage stage;

    public ErrorHandler() {
        try {
            // Init alert stage
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/alerts/exceptionAlert.fxml")));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage = new Stage();

            // Set stage settings
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinHeight(300);
            stage.setMinWidth(350);
            stage.setResizable(true);
            stage.centerOnScreen();

            // Store the controller to access later on
            errorHandlerController = loader.getController();
        } catch (IOException e) {
            logger.error("An error occurred while switching a scene!", e);
        }
    }

    public static void error(String message, Throwable throwable) {
        // Log the error
        logger.error(message, throwable);

        // Build the stack trace string
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().toString().replace("class ", "")).append(": ").append(throwable.getMessage()).append("\n");
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            sb.append("    at ").append(stackTraceElement.toString()).append("\n");
        }
        String stacktrace = sb.toString().strip();

        // Throw the error to the user
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setMessage(message + " - " + throwable.getMessage());
        errorHandler.setStacktrace(stacktrace);
        errorHandler.showAndWait();
    }

    public void setMessage(String message) {
        errorHandlerController.setMessage(message);
    }

    public void setStacktrace(String stacktrace) {
        errorHandlerController.setStacktrace(stacktrace);
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}

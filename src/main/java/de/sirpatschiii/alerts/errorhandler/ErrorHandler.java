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
import java.util.ArrayList;
import java.util.Arrays;
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
            System.exit(-1);
        }
    }

    public static void error(String message, Throwable throwable) {
        // Log the error
        logger.error(message, throwable);

        // Show the error handler to the user
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setMessage(getUserMessage(message, throwable));
        errorHandler.setStacktrace(getUserStacktrace(throwable));
        errorHandler.showAndWait();
    }

    private static String getUserMessage(String message, Throwable throwable) {
        String userMessage = "";
        String errorClass = Arrays.stream(throwable.getClass().toString().split("\\.")).toList().getLast();
        String errorCause = Arrays.stream(throwable.getCause().getClass().toString().split("\\.")).toList().getLast();

        // If the no custom message is defined, use the error message
        if (message.equals(throwable.getMessage())) {
            String errorMessage = throwable.getMessage();
            userMessage += errorClass + " - " + errorMessage;
        } else {
            userMessage += errorClass + " - " + message;
        }

        // Append cause if existent
        if (throwable.getCause() != null) {
            userMessage += " - caused by " + errorCause;
        }

        return userMessage;
    }

    private static String getUserStacktrace(Throwable throwable) {
        ArrayList<Throwable> causes = new ArrayList<>();

        // Get causes
        causes.addLast(throwable.getCause());
        while (causes.getLast().getCause() != null) {
            causes.addLast(causes.getLast().getCause());
        }

        // Build the stack trace string
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().toString().replace("class ", "")).append(": ").append(throwable.getMessage()).append("\n");
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            sb.append("    at ").append(stackTraceElement.toString()).append("\n");
        }

        // Add stack trace for causes
        for (Throwable thr : causes) {
            sb.append("caused by ").append(thr.getClass().toString().replace("class ", "")).append(": ").append(thr.getMessage()).append("\n");
            for (StackTraceElement stackTraceElement : thr.getStackTrace()) {
                sb.append("    at ").append(stackTraceElement.toString()).append("\n");
            }
        }

        return sb.toString().strip();
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

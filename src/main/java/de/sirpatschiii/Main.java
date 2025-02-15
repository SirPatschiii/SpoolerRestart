package de.sirpatschiii;

import de.sirpatschiii.alerts.errorhandler.ErrorBus;
import de.sirpatschiii.alerts.errorhandler.ErrorHandler;
import de.sirpatschiii.base.Configuration;
import de.sirpatschiii.base.SceneLoadException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    private static final Logger logger = Configuration.INSTANCE.getLogger();

    public static void main(String[] args) {
        logger.info("Program start");
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Set default global uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            ErrorBus.getInstance().reportError(throwable.getMessage(), throwable);
        });

        // Register the error listener
        ErrorBus.getInstance().setErrorListener(event -> {
            ErrorHandler.error(event.message(), event.exception());
        });

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/gui/GUI.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMinWidth(400);
            stage.setMinHeight(400);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setTitle("SpoolerRestart");
            stage.show();
        } catch (IOException e) {
            ErrorBus.getInstance().reportError(new SceneLoadException(e));
        }
    }
}

package de.sirpatschiii;

import de.sirpatschiii.base.Configuration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
            stage.getIcons().add(new Image("/icons/PrinterError.png"));
            stage.show();
        } catch (IOException e) {
            logger.error("An error occurred while switching a scene!", e);
        }
    }
}

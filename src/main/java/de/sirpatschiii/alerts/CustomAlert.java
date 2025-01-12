package de.sirpatschiii.alerts;

import de.sirpatschiii.base.Configuration;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.slf4j.Logger;

public abstract class CustomAlert {
    protected static final Logger logger = Configuration.INSTANCE.getLogger();

    protected Stage stage;

    public CustomAlert() {

    }

    public abstract void setHeader(String title);

    public abstract void setMessage(String message);

    public abstract void addMessage(String message);

    public abstract ButtonType getResult();

    public void showAndWait() {
        stage.showAndWait();
    }
}

package de.sirpatschiii.alerts;

import de.sirpatschiii.base.BaseWindowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Objects;

public class InfoAlertController extends BaseWindowController {
    @FXML
    Label lblHeader;
    @FXML
    Label lblMessage;
    @FXML
    Button btnAccept;

    private ButtonType result = null;

    @Override
    public void handleActionOnBTNExit(ActionEvent event) {
        result = ButtonType.CANCEL;
        closeStage(event);
    }

    public void handleActionOnBTNAccept(ActionEvent event) {
        result = ButtonType.OK;
        closeStage(event);
    }

    public void setHeader(String title) {
        lblHeader.setText(title);
    }

    public void setMessage(String message) {
        lblMessage.setText(message);
    }

    public void setBTNAcceptText(String text) {
        btnAccept.setText(text);
    }

    public ButtonType getResult() {
        return Objects.requireNonNullElseGet(result, () -> result = ButtonType.CANCEL);
    }

    public void closeStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

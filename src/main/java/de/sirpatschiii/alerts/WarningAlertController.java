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

public class WarningAlertController extends BaseWindowController {
    @FXML
    Label lblHeader;
    @FXML
    Label lblMessage;
    @FXML
    Button btnAccept;
    @FXML
    Button btnCancel;

    private ButtonType result = null;

    public void setHeader(String title) {
        lblHeader.setText(title);
    }

    public void setMessage(String message) {
        lblMessage.setText(message);
    }

    public void addMessage(String message) {
        lblMessage.setText(lblMessage.getText() + "\n" + message);
    }

    public void setBTNAcceptText(String text) {
        btnAccept.setText(text);
    }

    public void setBTNCancelText(String text) {
        btnCancel.setText(text);
    }

    public ButtonType getResult() {
        return Objects.requireNonNullElseGet(result, () -> result = ButtonType.CANCEL);
    }

    public void closeStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Logic for 'accept' button
    public void handleActionOnBTNAccept(ActionEvent event) {
        result = ButtonType.OK;
        closeStage(event);
    }

    // Logic for cancel button
    public void handleActionOnBTNCancel(ActionEvent event) {
        result = ButtonType.CANCEL;
        closeStage(event);
    }

    // Logic for exit button
    @Override
    public void handleActionOnBTNExit(ActionEvent event) {
        result = ButtonType.CANCEL;
        closeStage(event);
    }
}

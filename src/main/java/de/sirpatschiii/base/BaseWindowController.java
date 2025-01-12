package de.sirpatschiii.base;

import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;

public abstract class BaseWindowController {
    private static final Logger logger = Configuration.INSTANCE.getLogger();
    private double initialWindowX;
    private double initialWindowY;

    private double initialMouseX;
    private double initialMouseY;
    private double initialStageWidth;
    private double initialStageHeight;
    private double initialScreenMouseX;
    private double initialScreenMouseY;
    private double initialStageX;

    // Dragging the window
    public void handleMousePressedOnMenuBar(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (!stage.isMaximized()) {
            initialWindowX = event.getSceneX();
            initialWindowY = event.getSceneY();
        }
    }

    public void handleMouseDraggedOnMenuBar(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (stage.isMaximized()) {
            stage.setMinWidth(1280);
            stage.setMinHeight(800);
            ((Node) event.getSource()).getScene().getRoot().getStyleClass().remove("maximized");
            ((Node) event.getSource()).getScene().getRoot().getStyleClass().add("notMaximized");
            stage.setMaximized(false);
            if (stage.getWidth() < stage.getMinWidth()) {
                stage.setWidth(stage.getMinWidth());
            }
            if (stage.getHeight() < stage.getMinHeight()) {
                stage.setHeight(stage.getMinHeight());
            }
        } else {
            stage.setX(event.getScreenX() - initialWindowX);
            stage.setY(event.getScreenY() - initialWindowY);
        }
    }

    // Handle cases where the window hits a screen edge
    public void handleMouseReleasedOnMenuBar(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Return if not resizable
        if (!stage.isResizable()) {
            return;
        }

        // Top center
        if (event.getScreenY() == 0 && event.getScreenX() > 20 && Screen.getPrimary().getVisualBounds().getWidth() - event.getScreenX() > 20) {
            stage.setMinWidth(Screen.getPrimary().getVisualBounds().getWidth());
            stage.setMinHeight(Screen.getPrimary().getVisualBounds().getHeight());
            stage.setX(0);
            stage.setY(0);
            ((Node) event.getSource()).getScene().getRoot().getStyleClass().remove("notMaximized");
            ((Node) event.getSource()).getScene().getRoot().getStyleClass().add("maximized");
            stage.setMaximized(true);
            return;
        }
        if (stage.getY() <= 0) {
            stage.setY(0);
        }
    }

    // Reset focus from any element
    public void handleMouseClickedOnBorderPane(MouseEvent event) {
        Node node = (Node) event.getSource();
        node.requestFocus();
    }

    // Resizing the window
    public void handleMousePressedOnBorderPane(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        initialMouseX = event.getSceneX();
        initialMouseY = event.getSceneY();

        initialStageWidth = stage.getWidth();
        initialStageHeight = stage.getHeight();

        initialScreenMouseX = event.getScreenX();
        initialScreenMouseY = event.getScreenY();

        initialStageX = stage.getX();
    }

    public void handleMouseDraggedOnBorderPane(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Return if not resizable or in full screen
        if (!stage.isResizable() || stage.isMaximized()) {
            return;
        }

        double movedDistanceX = event.getScreenX() - initialScreenMouseX;
        double movedDistanceY = event.getScreenY() - initialScreenMouseY;

        // Resize on the right border
        if (initialStageWidth - initialMouseX <= 3 && initialMouseY >= 43 && initialStageHeight - initialMouseY >= 5) {
            if (stage.getMinWidth() < (initialMouseX + movedDistanceX)) {
                stage.setWidth(initialMouseX + movedDistanceX);
                return;
            }
        }
        // Resize the bottom right corner
        if ((initialStageWidth - initialMouseX <= 3 && initialStageHeight - initialMouseY <= 4) || (initialStageHeight - initialMouseY <= 3 && initialStageWidth - initialMouseX <= 4)) {
            if (stage.getMinWidth() < (initialMouseX + movedDistanceX)) {
                stage.setWidth(initialMouseX + movedDistanceX);
            }
            if (stage.getMinHeight() < (initialMouseY + movedDistanceY)) {
                stage.setHeight(initialMouseY + movedDistanceY);
            }
            return;
        }
        // Resize on the bottom border
        if (initialStageHeight - initialMouseY <= 3 && initialMouseX >= 5 && initialStageWidth - initialMouseX >= 5) {
            if (stage.getMinHeight() < (initialMouseY + movedDistanceY)) {
                stage.setHeight(initialMouseY + movedDistanceY);
                return;
            }
        }
        // Resize the bottom left corner
        if ((initialStageHeight - initialMouseY <= 3 && initialMouseX <= 4) || (initialMouseX <= 3 && initialStageHeight - initialMouseY <= 4)) {
            if (stage.getMinHeight() < (initialMouseY + movedDistanceY)) {
                stage.setHeight(initialMouseY + movedDistanceY);
            }
            if (stage.getMinWidth() < (initialStageWidth - (movedDistanceX))) {
                stage.setWidth(initialStageWidth - (movedDistanceX));
                stage.setX(initialStageX + (movedDistanceX));
            }
            return;
        }
        // Resize on the left border
        if (initialMouseX <= 3 && initialMouseY >= 43 && initialStageHeight - initialMouseY >= 5) {
            if (stage.getMinWidth() < (initialStageWidth - (movedDistanceX))) {
                stage.setWidth(initialStageWidth - (movedDistanceX));
                stage.setX(initialStageX + (movedDistanceX));
            }
        }
    }

    // Set the correct cursor type
    public void handleOnMouseMovedOnBorderPane(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        // Resize on the right border
        if (stage.isResizable() && !stage.isMaximized() && stageWidth - mouseX <= 3 && mouseY >= 43 && stageHeight - mouseY >= 5) {
            scene.setCursor(Cursor.E_RESIZE);
            return;
        }
        // Resize the bottom right corner
        if ((stage.isResizable() && !stage.isMaximized() && stageWidth - mouseX <= 3 && stageHeight - mouseY <= 4) || (stage.isResizable() && stageHeight - mouseY <= 3 && stageWidth - mouseX <= 4)) {
            scene.setCursor(Cursor.SE_RESIZE);
            return;
        }
        // Resize on the bottom border
        if (stage.isResizable() && !stage.isMaximized() && stageHeight - mouseY <= 3 && mouseX >= 5 && stageWidth - mouseX >= 5) {
            scene.setCursor(Cursor.S_RESIZE);
            return;
        }
        // Resize the bottom left corner
        if ((stage.isResizable() && !stage.isMaximized() && stageHeight - mouseY <= 3 && mouseX <= 4) || (stage.isResizable() && mouseX <= 3 && stageHeight - mouseY <= 4)) {
            scene.setCursor(Cursor.SW_RESIZE);
            return;
        }
        // Resize on the left border
        if (stage.isResizable() && !stage.isMaximized() && mouseX <= 3 && mouseY >= 43 && stageHeight - mouseY >= 5) {
            scene.setCursor(Cursor.W_RESIZE);
            return;
        }

        // If no cases triggered set default
        scene.setCursor(Cursor.DEFAULT);
    }

    // Set the default cursor after resize action
    public void handleMouseReleasedOnBorderPane(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene().getWindow().getScene();
        scene.setCursor(Cursor.DEFAULT);
    }

    public void handleDoubleClickOnMenuBar(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Return if not resizable
        if (!stage.isResizable()) {
            return;
        }

        if (event.getClickCount() == 2 && !stage.isMaximized()) {
            stage.setMinWidth(Screen.getPrimary().getVisualBounds().getWidth());
            stage.setMinHeight(Screen.getPrimary().getVisualBounds().getHeight());
            stage.setX(0);
            stage.setY(0);
            ((Node) event.getSource()).getScene().getRoot().getStyleClass().remove("notMaximized");
            ((Node) event.getSource()).getScene().getRoot().getStyleClass().add("maximized");
            stage.setMaximized(true);
            return;
        }
        if (event.getClickCount() == 2 && stage.isMaximized()) {
            stage.setMinWidth(1280);
            stage.setMinHeight(800);
            ((Node) event.getSource()).getScene().getRoot().getStyleClass().remove("maximized");
            ((Node) event.getSource()).getScene().getRoot().getStyleClass().add("notMaximized");
            stage.setMaximized(false);
            if (stage.getWidth() < stage.getMinWidth()) {
                stage.setWidth(stage.getMinWidth());
            }
            if (stage.getHeight() < stage.getMinHeight()) {
                stage.setHeight(stage.getMinHeight());
            }
        }
    }

    // Logic for minimize button
    public void handleActionOnBTNMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    // Logic for exit button
    public void handleActionOnBTNExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        logger.info("Program end");
        stage.close();
    }
}

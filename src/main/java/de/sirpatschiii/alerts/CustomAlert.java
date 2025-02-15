package de.sirpatschiii.alerts;

import de.sirpatschiii.base.Configuration;
import javafx.geometry.Dimension2D;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;

public abstract class CustomAlert {
    protected static final Logger logger = Configuration.INSTANCE.getLogger();

    protected Stage stage;

    public abstract void setHeader(String title);

    public abstract void setMessage(String message);

    public abstract ButtonType getResult();

    public void showAndWait() {
        stage.showAndWait();
    }

    protected static Dimension2D getWrapped16by9Dimensions(String str) {
        Text text = new Text(str);
        text.setFont(Font.font("Arial", 14));

        // Measure the natural size (no wrapping)
        text.setWrappingWidth(0);
        double naturalWidth = text.getLayoutBounds().getWidth();
        double naturalHeight = text.getLayoutBounds().getHeight();
        double targetRatio = 16.0 / 9.0;

        // If the natural one-line layout has an aspect ratio less than 16:9, wrapping won’t help.
        if (naturalWidth / naturalHeight < targetRatio) {
            return new Dimension2D(naturalHeight * targetRatio, naturalHeight);
        }

        // Otherwise, try to find a wrapping width so that:
        // wrappingWidth / wrappedHeight == 16/9
        double low = 50;
        double high = naturalWidth;
        double mid = 0;
        double tolerance = 0.5;

        while (high - low > tolerance) {
            mid = (low + high) / 2.0;
            text.setWrappingWidth(mid);
            double wrappedHeight = text.getLayoutBounds().getHeight();
            double currentRatio = mid / wrappedHeight;

            if (currentRatio < targetRatio) {
                low = mid;
            } else {
                high = mid;
            }
        }

        // Use the found wrapping width.
        text.setWrappingWidth(mid);
        double finalHeight = text.getLayoutBounds().getHeight();
        return new Dimension2D(mid, finalHeight);
    }
}

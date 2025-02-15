module de.sirpatschiii {
    // Requires statements for external dependencies
    requires com.sun.jna.platform;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;

    // Export package
    exports de.sirpatschiii;
    exports de.sirpatschiii.alerts;
    exports de.sirpatschiii.base;
    exports de.sirpatschiii.gui;
    exports de.sirpatschiii.handle64;
    exports de.sirpatschiii.util;

    // Open package for fxml
    opens de.sirpatschiii.gui to javafx.fxml;
    opens de.sirpatschiii.alerts to javafx.fxml;
}

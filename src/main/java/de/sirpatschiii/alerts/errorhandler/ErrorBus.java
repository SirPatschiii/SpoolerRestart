package de.sirpatschiii.alerts.errorhandler;

import javafx.application.Platform;

import java.util.function.Consumer;

public class ErrorBus {
    private static ErrorBus instance;
    private Consumer<ErrorEvent> errorListener;

    private ErrorBus() {

    }

    public static synchronized ErrorBus getInstance() {
        if (instance == null) {
            instance = new ErrorBus();
        }
        return instance;
    }

    public void setErrorListener(Consumer<ErrorEvent> listener) {
        this.errorListener = listener;
    }

    public void reportError(Throwable throwable) {
        if (errorListener != null) {
            Platform.runLater(() -> errorListener.accept(new ErrorEvent(throwable.getMessage(), throwable)));
        }
    }

    public void reportError(String message, Throwable throwable) {
        if (errorListener != null) {
            Platform.runLater(() -> errorListener.accept(new ErrorEvent(message, throwable)));
        }
    }
}

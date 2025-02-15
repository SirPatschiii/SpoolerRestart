package de.sirpatschiii.base;

public class SceneLoadException extends RuntimeException {
    public SceneLoadException(Throwable cause) {
        super("An error occurred while switching a scene!", cause);
    }
}

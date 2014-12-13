package org.vaadin.vwscdn.client;

public enum PublishState {
    UNKNOWN, // We don't have idea yet
    NOT_FOUND, // No widgetset exists with an id and has not been compiled
    AVAILABLE, // Compiled and published
    COMPILED, // Compiled succesfully, but not yet available
    COMPILING, // Currently compiling the widgetset
    ERROR; // Widgetset was compiled, but error occured

}

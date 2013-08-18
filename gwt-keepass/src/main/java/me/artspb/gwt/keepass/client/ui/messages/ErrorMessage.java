package me.artspb.gwt.keepass.client.ui.messages;

import com.github.gwtbootstrap.client.ui.constants.AlertType;

/**
 * @author Artem Khvastunov
 */
public class ErrorMessage extends Message {

    public ErrorMessage(String message, boolean criticalError) {
        super(message, criticalError);
        setType(AlertType.ERROR);
        setHeading("Error!");
    }

    public ErrorMessage(String message) {
        this(message, false);
    }
}

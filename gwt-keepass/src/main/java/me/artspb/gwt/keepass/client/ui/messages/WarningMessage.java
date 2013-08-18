package me.artspb.gwt.keepass.client.ui.messages;

import com.github.gwtbootstrap.client.ui.constants.AlertType;

/**
 * @author Artem Khvastunov
 */
public class WarningMessage extends Message {

    public WarningMessage(String message) {
        super(message);
        setType(AlertType.WARNING);
        setHeading("Warning!");
    }
}

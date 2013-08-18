package me.artspb.gwt.keepass.client.ui.messages;

import com.github.gwtbootstrap.client.ui.constants.AlertType;

/**
 * @author Artem Khvastunov
 */
public class InfoMessage extends Message {

    public InfoMessage(String message) {
        super(message);
        setType(AlertType.INFO);
        setHeading("Information.");
    }
}

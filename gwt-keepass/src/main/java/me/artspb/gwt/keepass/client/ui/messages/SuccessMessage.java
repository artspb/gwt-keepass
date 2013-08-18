package me.artspb.gwt.keepass.client.ui.messages;

import com.github.gwtbootstrap.client.ui.constants.AlertType;

/**
 * @author Artem Khvastunov
 */
public class SuccessMessage extends Message {

    public SuccessMessage(String message) {
        super(message);
        setType(AlertType.SUCCESS);
        setHeading("Success!");
    }
}

package me.artspb.gwt.keepass.client.ui.messages;

import com.github.gwtbootstrap.client.ui.Alert;

/**
 * @author Artem Khvastunov
 */
public class Message extends Alert {

    private boolean criticalError;

    protected Message(String message, boolean criticalError) {
        super(message);
        setAnimation(true);
        this.criticalError = criticalError;
    }

    protected Message(String message) {
        this(message, false);
    }

    public boolean isCriticalError() {
        return criticalError;
    }
}

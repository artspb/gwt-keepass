package me.artspb.gwt.keepass.client.interfaces;

/**
 * @author Artem Khvastunov
 */
public interface KeyAcceptor extends ErrorMessageAcceptor {

    void setKey(byte[] key);
}

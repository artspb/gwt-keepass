package me.artspb.gwt.keepass.client.interfaces;

/**
 * @author Artem Khvastunov
 */
public interface KeyAcceptor extends MessageAcceptor {

    void setKey(byte[] key);
}

package me.artspb.gwt.keepass.client.interfaces;

/**
 * @author Artem Khvastunov
 */
public interface CredentialsProvider {

    byte[] getKey();

    String getPassword();
}

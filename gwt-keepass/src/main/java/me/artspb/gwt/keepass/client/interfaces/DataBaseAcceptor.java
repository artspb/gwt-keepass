package me.artspb.gwt.keepass.client.interfaces;

import pl.sind.keepass.kdb.KeePassDataBase;

/**
 * @author Artem Khvastunov
 */
public interface DataBaseAcceptor extends MessageAcceptor {

    void setDataBase(KeePassDataBase dataBase);
}

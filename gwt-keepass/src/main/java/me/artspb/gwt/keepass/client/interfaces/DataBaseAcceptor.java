package me.artspb.gwt.keepass.client.interfaces;

import pl.sind.keepass.kdb.KeePassDataBase;

/**
 * @author Artem Khvastunov
 */
public interface DataBaseAcceptor extends ErrorMessageAcceptor {

    void setDataBase(KeePassDataBase dataBase);
}

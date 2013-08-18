package me.artspb.gwt.keepass.client.handler;

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Int8Array;
import me.artspb.gwt.keepass.client.interfaces.CredentialsProvider;
import me.artspb.gwt.keepass.client.interfaces.DataBaseAcceptor;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import pl.sind.keepass.exceptions.KeePassDataBaseException;
import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.kdb.KeePassDataBaseFactory;

/**
 * @author Artem Khvastunov
 */
public class DataBaseLoadEndHandler implements LoadEndHandler {

    private final FileReader reader;
    private final CredentialsProvider holder;
    private final DataBaseAcceptor acceptor;

    public DataBaseLoadEndHandler(FileReader reader, CredentialsProvider holder, DataBaseAcceptor acceptor) {
        this.reader = reader;
        this.holder = holder;
        this.acceptor = acceptor;
    }

    public void onLoadEnd(LoadEndEvent event) {
        if (reader.getError() == null) {
            try {
                ArrayBuffer buffer = reader.getArrayBufferResult();
                Int8Array array = Int8ArrayNative.create(buffer);
                byte[] bytes = new byte[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    bytes[i] = array.get(i);
                }
                String password = holder.getPassword();
                KeePassDataBase base = KeePassDataBaseFactory.loadDataBase(bytes, holder.getKey(), password.length() == 0 ? null : password);
                acceptor.setDataBase(base);
            } catch (KeePassDataBaseException e) {
                acceptor.setErrorMessage("error: " + e.getMessage());
            }
        } else {
            acceptor.setErrorMessage("file upload error code: " + reader.getError().getCode());
        }
    }
}
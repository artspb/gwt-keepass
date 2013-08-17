package me.artspb.gwt.keepass.client.handler;

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Int8Array;
import me.artspb.gwt.keepass.client.interfaces.KeyAcceptor;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

/**
 * @author Artem Khvastunov
 */
public class KeyLoadEndHandler implements LoadEndHandler {

    private final FileReader reader;
    private final KeyAcceptor acceptor;

    public KeyLoadEndHandler(FileReader reader, KeyAcceptor acceptor) {
        this.reader = reader;
        this.acceptor = acceptor;
    }

    public void onLoadEnd(LoadEndEvent event) {
        if (reader.getError() == null) {
            ArrayBuffer buffer = reader.getArrayBufferResult();
            Int8Array array = Int8ArrayNative.create(buffer);
            byte[] key = new byte[array.length()];
            for (int i = 0; i < array.length(); i++) {
                key[i] = array.get(i);
            }
            acceptor.setKey(key);
        } else {
            acceptor.setMessage("file upload error code: " + reader.getError().getCode());
        }
    }
}

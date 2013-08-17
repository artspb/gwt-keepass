package me.artspb.gwt.keepass.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import me.artspb.gwt.keepass.client.handler.DataBaseLoadEndHandler;
import me.artspb.gwt.keepass.client.handler.KeyLoadEndHandler;
import me.artspb.gwt.keepass.client.interfaces.CredentialsProvider;
import me.artspb.gwt.keepass.client.interfaces.DataBaseAcceptor;
import me.artspb.gwt.keepass.client.interfaces.MessageAcceptor;
import me.artspb.gwt.keepass.client.interfaces.KeyAcceptor;
import org.vectomatic.file.File;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

/**
 * @author Artem Khvastunov
 */
public class KeePassEditor implements EntryPoint, ClickHandler, CredentialsProvider, KeyAcceptor, DataBaseAcceptor, MessageAcceptor {

    private final FileUploadExt dbUpload = new FileUploadExt(false);
    private final FileUploadExt keyUpload = new FileUploadExt(false);
    private final PasswordTextBox passwordTextBox = new PasswordTextBox();
    private final Button button = new Button("load");
    private final Label label = new Label();

    private final KeePassDataBaseV1Tree tree = new KeePassDataBaseV1Tree();

    private final FileReader keyReader = new FileReader();
    private final FileReader dbReader = new FileReader();

    private byte[] key = null;

    public void onModuleLoad() {
        keyReader.addLoadEndHandler(new KeyLoadEndHandler(keyReader, this));
        dbReader.addLoadEndHandler(new DataBaseLoadEndHandler(dbReader, this, this));
        button.addClickHandler(this);

        RootPanel.get("slot1").add(dbUpload);
        RootPanel.get("slot2").add(keyUpload);
        RootPanel.get("slot3").add(passwordTextBox);
        RootPanel.get("slot4").add(button);
        RootPanel.get("slot5").add(label);

        RootPanel.get("tree").add(tree);
    }

    public void onClick(ClickEvent event) {
        label.setText("");

        File keyFile = keyUpload.getFiles().getItem(0);
        if (keyFile != null) {
            keyReader.readAsArrayBuffer(keyFile);
        } else {
            setKey(null);
        }
    }

    public byte[] getKey() {
        return key;
    }

    public String getPassword() {
        return passwordTextBox.getText();
    }

    public void setDataBase(KeePassDataBase dataBase) {
        tree.apply((KeePassDataBaseV1) dataBase);
    }

    public void setKey(byte[] key) {
        this.key = key;
        File dbFile = dbUpload.getFiles().getItem(0);
        dbReader.readAsArrayBuffer(dbFile);
    }

    public void setMessage(String error) {
        label.setText(error);
    }
}
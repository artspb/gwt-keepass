package me.artspb.gwt.keepass.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.*;
import me.artspb.gwt.keepass.client.handler.DataBaseLoadEndHandler;
import me.artspb.gwt.keepass.client.handler.KeyLoadEndHandler;
import me.artspb.gwt.keepass.client.interfaces.CredentialsProvider;
import me.artspb.gwt.keepass.client.interfaces.DataBaseAcceptor;
import me.artspb.gwt.keepass.client.interfaces.MessageAcceptor;
import me.artspb.gwt.keepass.client.interfaces.KeyAcceptor;
import me.artspb.gwt.keepass.client.ui.EntryVerticalPanel;
import me.artspb.gwt.keepass.client.ui.KeePassDataBaseTreeItem;
import me.artspb.gwt.keepass.client.ui.KeePassDataBaseV1Tree;
import org.vectomatic.file.File;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.kdb.v1.Entry;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

/**
 * @author Artem Khvastunov
 */
public class KeePassEditor implements EntryPoint, ClickHandler, CredentialsProvider, KeyAcceptor, DataBaseAcceptor, MessageAcceptor, SelectionHandler<TreeItem> {

    private final FileUploadExt dbUpload = new FileUploadExt(false);
    private final FileUploadExt keyUpload = new FileUploadExt(false);
    private final PasswordTextBox passwordTextBox = new PasswordTextBox();
    private final Button button = new Button("load");
    private final Label label = new Label();

    private final KeePassDataBaseV1Tree tree = new KeePassDataBaseV1Tree();

    private final SplitLayoutPanel mainPanel = new SplitLayoutPanel();
    private final Panel uploadPanel = new HorizontalPanel();
    private final Panel entryPanel = new FlowPanel();

    private final FileReader keyReader = new FileReader();
    private final FileReader dbReader = new FileReader();

    private byte[] key = null;

    public void onModuleLoad() {
        keyReader.addLoadEndHandler(new KeyLoadEndHandler(keyReader, this));
        dbReader.addLoadEndHandler(new DataBaseLoadEndHandler(dbReader, this, this));
        button.addClickHandler(this);
        tree.addSelectionHandler(this);

        uploadPanel.add(dbUpload);
        uploadPanel.add(keyUpload);
        uploadPanel.add(passwordTextBox);
        uploadPanel.add(button);

        mainPanel.addNorth(uploadPanel, 384);
        mainPanel.addSouth(label, 100);
        mainPanel.addWest(tree, 128);
        mainPanel.add(entryPanel);

        RootLayoutPanel.get().add(mainPanel);
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

    public void onSelection(SelectionEvent<TreeItem> event) {
        KeePassDataBaseTreeItem item = (KeePassDataBaseTreeItem) event.getSelectedItem();
        if (item.isEntry()) {
            Entry entry = item.getEntry();
            entryPanel.add(new EntryVerticalPanel(entry));
        }
    }
}
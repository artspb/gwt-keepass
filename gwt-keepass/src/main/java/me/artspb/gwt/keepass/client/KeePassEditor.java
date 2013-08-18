package me.artspb.gwt.keepass.client;

import com.github.gwtbootstrap.client.ui.*;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TreeItem;
import me.artspb.gwt.keepass.client.handler.DataBaseLoadEndHandler;
import me.artspb.gwt.keepass.client.handler.KeyLoadEndHandler;
import me.artspb.gwt.keepass.client.interfaces.CredentialsProvider;
import me.artspb.gwt.keepass.client.interfaces.DataBaseAcceptor;
import me.artspb.gwt.keepass.client.interfaces.KeyAcceptor;
import me.artspb.gwt.keepass.client.ui.BootstrapFileUpload;
import me.artspb.gwt.keepass.client.ui.DataBaseTreeItem;
import me.artspb.gwt.keepass.client.ui.DataBaseV1Tree;
import me.artspb.gwt.keepass.client.ui.EntryVerticalPanel;
import me.artspb.gwt.keepass.client.ui.messages.Message;
import me.artspb.gwt.keepass.client.ui.messages.SuccessMessage;
import me.artspb.gwt.keepass.client.ui.messages.WarningMessage;
import org.vectomatic.file.File;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.kdb.v1.Entry;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

/**
 * @author Artem Khvastunov
 */
public class KeePassEditor implements EntryPoint, ClickHandler, CredentialsProvider, KeyAcceptor, DataBaseAcceptor, SelectionHandler<TreeItem> {

    private final FileUploadExt dbUpload = new BootstrapFileUpload();
    private final FileUploadExt keyUpload = new BootstrapFileUpload();
    private final PasswordTextBox passwordTextBox = new PasswordTextBox();
    private final Button button = new Button();
    private final FlowPanel alertPanel = new FlowPanel();

    private final DataBaseV1Tree tree = new DataBaseV1Tree();
    private final FlowPanel entryPanel = new FlowPanel();

    private final FileReader keyReader = new FileReader();
    private final FileReader dbReader = new FileReader();

    private byte[] key = null;

    public void onModuleLoad() {
        button.setText("load");
        button.setLoadingText("loading...");
        button.setCompleteText("loaded");
        button.setBlock(true);

        keyReader.addLoadEndHandler(new KeyLoadEndHandler(keyReader, this));
        dbReader.addLoadEndHandler(new DataBaseLoadEndHandler(dbReader, this, this));

        button.addClickHandler(this);
        tree.addSelectionHandler(this);

        Container container = new FluidContainer();
        Row row = new FluidRow();
        row.add(new Column(6, alertPanel));
        container.add(row);
        row = new FluidRow();
        row.add(new Column(3, dbUpload));
        row.add(new Column(3, keyUpload));
        container.add(row);
        row = new FluidRow();
        row.add(new Column(3, passwordTextBox));
        row.add(new Column(3, button));
        container.add(row);
        row = new FluidRow();
        row.add(new Column(6));
        container.add(row);
        row = new FluidRow();
        row.add(new Column(3, tree));
        row.add(new Column(3, entryPanel));
        container.add(row);
        RootLayoutPanel.get().add(container);
    }

    public void onClick(ClickEvent event) {
        reset();
        button.state().loading();

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
        button.state().complete();
        showMessage(new SuccessMessage("Database has been successfully loaded."));
        tree.apply((KeePassDataBaseV1) dataBase);
    }

    public void setKey(byte[] key) {
        this.key = key;
        File dbFile = dbUpload.getFiles().getItem(0);
        if (dbFile != null) {
            dbReader.readAsArrayBuffer(dbFile);
        } else {
            reset(new WarningMessage("Nothing to load."));
        }
    }

    public void setMessage(Message message) {
        if (message.isCriticalError()) {
            reset(message);
        } else {
            showMessage(message);
        }
    }

    public void onSelection(SelectionEvent<TreeItem> event) {
        DataBaseTreeItem item = (DataBaseTreeItem) event.getSelectedItem();
        if (item.isEntry()) {
            Entry entry = item.getEntry();
            entryPanel.clear();
            entryPanel.add(new EntryVerticalPanel(entry));
        }
    }

    private void reset() {
        reset(null);
    }

    private void reset(Message message) {
        showMessage(message);
        tree.clear();
        entryPanel.clear();
        button.state().reset();
    }

    private void showMessage(Message message) {
        if (message != null) {
            alertPanel.add(message);
        } else {
            alertPanel.clear();
        }
    }
}
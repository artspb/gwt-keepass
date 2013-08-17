package me.artspb.gwt.keepass.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.user.client.ui.*;
import org.vectomatic.file.File;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import pl.sind.keepass.exceptions.KeePassDataBaseException;
import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.kdb.KeePassDataBaseFactory;
import pl.sind.keepass.kdb.v1.Entry;
import pl.sind.keepass.kdb.v1.Group;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class KeePassEditor implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final FileUploadExt dbUpload = new FileUploadExt(false);
        final FileUploadExt keyUpload = new FileUploadExt(false);
        final PasswordTextBox passwordTextBox = new PasswordTextBox();
        final Button button = new Button("load");
        final Label label = new Label();
        final Tree tree = new Tree();

        final byte[][] key = {new byte[0]};

        final FileReader dbReader = new FileReader();
        dbReader.addLoadEndHandler(new LoadEndHandler() {
            public void onLoadEnd(LoadEndEvent event) {
                String s = "";
                if (dbReader.getError() == null) {
                    tree.clear();
                    try {
                        ArrayBuffer buffer = dbReader.getArrayBufferResult();
                        Int8Array array = Int8ArrayNative.create(buffer);
                        byte[] bytes = new byte[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            bytes[i] = array.get(i);
                        }

                        String password = passwordTextBox.getText();
                        KeePassDataBase base = KeePassDataBaseFactory.loadDataBase(bytes, key[0].length == 0 ? null : key[0], password.length() == 0 ? null : password);
                        if (base instanceof KeePassDataBaseV1) {
                            KeePassDataBaseV1 v1 = (KeePassDataBaseV1) base;
                            List<Entry> entries = v1.getEntries();
                            List<Group> groups = v1.getGroups();
                            s = "entries: " + entries.size() + ", groups: " + groups.size();

                            HashMap<Integer, HasTreeItems> treeGroups = new HashMap<Integer, HasTreeItems>();
                            Map<Integer, HasTreeItems> map = new HashMap<Integer, HasTreeItems>();
                            map.put(-1, tree);
                            for (Group group : groups) {
                                TreeItem item = new TreeItem(group.getGroupName().getText());
                                short level = group.getLevel().getLevel();
                                HasTreeItems parent = map.get(level - 1);
                                parent.addItem(item);
                                map.put((int) level, item);
                                treeGroups.put(group.getGroupId().getId(), item);
                            }
                            for (Entry entry : entries) {
                                if (!entry.getTitle().getText().equals("Meta-Info")) {
                                    HasTreeItems parent = treeGroups.get(entry.getGroupId().getId());
                                    parent.addTextItem(entry.getTitle().getText());
                                }
                            }
                        }
                    } catch (KeePassDataBaseException e) {
                        s = "error: " + e.getMessage();
                    }

                } else {
                    s =  "file upload error code: " + dbReader.getError().getCode();
                }
                label.setText(s);
            }
        });

        final FileReader keyReader = new FileReader();
        keyReader.addLoadEndHandler(new LoadEndHandler() {
            public void onLoadEnd(LoadEndEvent event) {
                String s = "";
                if (keyReader.getError() == null) {
                    ArrayBuffer buffer = keyReader.getArrayBufferResult();
                    Int8Array array = Int8ArrayNative.create(buffer);
                    byte[] bytes = new byte[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        bytes[i] = array.get(i);
                    }
                    key[0] = bytes;
                } else {
                    s =  "file upload error code: " + keyReader.getError().getCode();
                }
                label.setText(s);
            }
        });

        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                label.setText("");
                File keyFile = keyUpload.getFiles().getItem(0);
                keyReader.readAsArrayBuffer(keyFile);
                File dbFile = dbUpload.getFiles().getItem(0);
                dbReader.readAsArrayBuffer(dbFile);
            }
        });

        RootPanel.get("slot1").add(dbUpload);
        RootPanel.get("slot2").add(keyUpload);
        RootPanel.get("slot3").add(passwordTextBox);
        RootPanel.get("slot4").add(button);
        RootPanel.get("slot5").add(label);

        RootPanel.get("tree").add(tree);
    }
}
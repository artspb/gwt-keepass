package me.artspb.gwt.keepass.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.vectomatic.arrays.ArrayBuffer;
import org.vectomatic.arrays.DataView;
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

import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class KeePassEditor implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Button button = new Button("Click me");
        final Label label = new Label();
        final FileUploadExt fileUpload = new FileUploadExt();

        final FileReader reader = new FileReader();

        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                for (File file : fileUpload.getFiles()) {
                    reader.readAsArrayBuffer(file);

                    reader.addLoadEndHandler(new LoadEndHandler() {
                        public void onLoadEnd(LoadEndEvent event) {
                            if (reader.getError() == null) {
                                String s = "";

                                try {
                                    ArrayBuffer buffer = reader.getArrayBufferResult();
                                    DataView dataView = DataView.createDataView(buffer);
                                    byte[] bytes = new byte[dataView.getByteLength()];
                                    for (int i = 0; i < dataView.getByteLength(); i++) {
                                        bytes[i] = dataView.getInt8(i);
                                    }

                                    KeePassDataBase base = KeePassDataBaseFactory.loadDataBase(bytes, null, "testing");
                                    if (base instanceof KeePassDataBaseV1) {
                                        KeePassDataBaseV1 v1 = (KeePassDataBaseV1) base;
                                        List<Entry> entries = v1.getEntries();
                                        List<Group> groups = v1.getGroups();
                                    }
                                } catch (KeePassDataBaseException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }

                                label.setText(s);
                            }
                        }
                    });
                }
            }
        });

//        SplitLayoutPanel p = new SplitLayoutPanel();
//        p.addWest(new HTML("navigation"), 128);
//        p.addNorth(new HTML("list"), 384);
//        p.add(new HTML("details"));

        RootPanel.get("slot2").add(button);
        RootPanel.get("slot3").add(label);
        RootPanel.get("slot1").add(fileUpload);
    }
}
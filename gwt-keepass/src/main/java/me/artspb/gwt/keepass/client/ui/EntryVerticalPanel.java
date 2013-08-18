package me.artspb.gwt.keepass.client.ui;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import pl.sind.keepass.kdb.v1.Entry;

import java.util.Date;

/**
 * @author Artem Khvastunov
 */
public class EntryVerticalPanel extends VerticalPanel {

    private final Entry entry;

    public EntryVerticalPanel(Entry entry) {
        this.entry = entry;

        add(getPanel("groupId", String.valueOf(entry.getGroupId().getId())));
        add(getPanel("imageId", String.valueOf(entry.getImageId().getId())));

        add(getPanel("title", entry.getTitle().getText()));
        add(getPanel("url", entry.getUrl().getText()));
        add(getPanel("username", entry.getUsername().getText()));
        add(getPanel("password", entry.getPassword().getText()));
        add(getPanel("notes", entry.getNotes().getText()));

        add(getPanel("creationTime", entry.getCreationTime().getDate()));
        add(getPanel("lastModificationTime", entry.getLastModificationTime().getDate()));
        add(getPanel("lastAccessTime", entry.getLastAccessTime().getDate()));
        add(getPanel("expirationTime", entry.getExpirationTime().getDate()));
    }

    private HorizontalPanel getPanel(String name, String content) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Label(name));
        TextBox textBox = new TextBox();
        textBox.setText(content);
        textBox.setReadOnly(true);
        panel.add(textBox);
        return panel;
    }

    private HorizontalPanel getPanel(String name, Date content) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Label(name));
        panel.add(new ReadOnlyDateTimeBox(content));
        return panel;
    }

    private HorizontalPanel getPanel(String name, Widget widget) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Label(name));
        panel.add(widget);
        return panel;
    }
}

package me.artspb.gwt.keepass.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import pl.sind.keepass.kdb.v1.Entry;

/**
 * @author Artem Khvastunov
 */
public class EntryVerticalPanel extends VerticalPanel {
    
    private final Entry entry;

    public EntryVerticalPanel(Entry entry) {
        this.entry = entry;

        add(getPanel("groupId", String.valueOf(entry.getGroupId().getId())));
        add(getPanel("imageId", String.valueOf(entry.getImageId().getId())));
        add(getPanel("title", String.valueOf(entry.getTitle().getText())));
        add(getPanel("url", String.valueOf(entry.getUrl().getText())));
        add(getPanel("username", String.valueOf(entry.getUsername().getText())));
        add(getPanel("password", String.valueOf(entry.getPassword().getText())));
        add(getPanel("notes", String.valueOf(entry.getNotes().getText())));
        add(getPanel("creationTime", String.valueOf(entry.getCreationTime().getDate().toString())));
        add(getPanel("lastModificationTime", String.valueOf(entry.getLastModificationTime().getDate().toString())));
        add(getPanel("lastAccessTime", String.valueOf(entry.getLastAccessTime().getDate().toString())));
        add(getPanel("expirationTime", String.valueOf(entry.getExpirationTime().getDate().toString())));
    }

    private HorizontalPanel getPanel(String name, String content) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new HTML(name));
        TextBox textBox = new TextBox();
        textBox.setText(content);
        panel.add(textBox);
        return panel;
    }
}

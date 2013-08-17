package me.artspb.gwt.keepass.client.ui;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.TreeItem;
import pl.sind.keepass.kdb.v1.Entry;
import pl.sind.keepass.kdb.v1.Group;

/**
 * @author Artem Khvastunov
 */
public class KeePassDataBaseTreeItem extends TreeItem {

    private final Entry entry;
    private final Group group;

    private static SafeHtml getSafeHtml(String text) {
        return new SafeHtmlBuilder().appendEscaped(text).toSafeHtml();
    }

    public KeePassDataBaseTreeItem(Entry entry) {
        super(getSafeHtml(entry.getTitle().getText()));
        this.entry = entry;
        this.group = null;
    }

    public KeePassDataBaseTreeItem(Group group) {
        super(getSafeHtml(group.getGroupName().getText()));
        this.entry = null;
        this.group = group;
    }

    public boolean isGroup() {
        return group != null;
    }

    public boolean isEntry() {
        return entry != null;
    }

    public Entry getEntry() {
        return entry;
    }

    public Group getGroup() {
        return group;
    }
}

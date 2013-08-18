package me.artspb.gwt.keepass.client.ui;

import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import pl.sind.keepass.kdb.v1.Entry;
import pl.sind.keepass.kdb.v1.Group;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Artem Khvastunov
 */
public class DataBaseV1Tree extends Tree {

    private KeePassDataBaseV1 base;

    public void apply(KeePassDataBaseV1 base) {
        clear();
        this.base = base;

        List<Group> groups = base.getGroups();
        Map<Integer, HasTreeItems> treeGroups = new HashMap<Integer, HasTreeItems>();
        Map<Integer, HasTreeItems> map = new HashMap<Integer, HasTreeItems>();
        map.put(-1, this);
        for (Group group : groups) {
            TreeItem item = new DataBaseTreeItem(group);
            short level = group.getLevel().getLevel();
            HasTreeItems parent = map.get(level - 1);
            parent.addItem(item);
            map.put((int) level, item);
            treeGroups.put(group.getGroupId().getId(), item);
        }
        List<Entry> entries = base.getEntries();
        for (Entry entry : entries) {
            if (!entry.getTitle().getText().equals("Meta-Info")) {
                HasTreeItems parent = treeGroups.get(entry.getGroupId().getId());
                parent.addItem(new DataBaseTreeItem(entry));
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        base = null;
    }
}

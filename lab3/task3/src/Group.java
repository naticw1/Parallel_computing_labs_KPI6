import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class Group {
    private final String groupName;
    public ConcurrentHashMap<Integer, List<String>> groupList = new ConcurrentHashMap<>();

    public Group(String groupName, int sizeOfGroup) {
        this.groupName = groupName;
        generateGroupList(sizeOfGroup);
    }

    private void generateGroupList(int sizeOfGroup) {
        for (int i = 0; i < sizeOfGroup; i++) {
            this.groupList.put(i + 1, new CopyOnWriteArrayList<>());
        }
    }

    public String getGroupName() {
        return groupName;
    }
}


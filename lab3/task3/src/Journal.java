
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Journal {
    public HashMap<String, Group> groups = new HashMap<>();

    public Journal() {
        initializeGroups();
    }

    // Separate method to initialize groups
    private void initializeGroups() {
        addGroup("ІP-11", 25);
        addGroup("ІP-13", 30);
        addGroup("ІP-15", 26);
    }

    // Helper method to add a new group
    private void addGroup(String groupName, int size) {
        Group group = new Group(groupName, size);
        groups.put(group.getGroupName(), group);
    }

    public void addMark(String groupName, Integer studentName, String mark) {
        List<String> studentMarks = this.groups.get(groupName).groupList.get(studentName);
        if (studentMarks != null) {
            synchronized (studentMarks) {
                studentMarks.add(mark);
            }
        }
    }


    public void show() {
        for (String groupName : groups.keySet().stream().sorted().collect(Collectors.toList())) {
            System.out.printf("Group name: %6s\n", groupName);
            for (Integer studentName :
                    groups.get(groupName).groupList.keySet().stream().sorted().collect(Collectors.toList())) {
                System.out.printf("Student %5s %5s", studentName, "-");
                for (String mark : groups.get(groupName).groupList.get(studentName)) {
                    System.out.printf("%30s", mark);
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private void displayGroup(String groupName) {
        System.out.printf("Group name: %s\n", groupName);
        Group group = groups.get(groupName);
        group.groupList.keySet().stream().sorted().forEach(studentId ->
                displayStudent(group, studentId));
    }

    // Display marks for a single student
    private void displayStudent(Group group, Integer studentId) {
        List<String> marks = group.groupList.get(studentId);
        String studentMarks = marks.stream()
                .collect(Collectors.joining(" | ", "Marks: ", ""));
        System.out.printf("Student %d - %s\n", studentId, studentMarks);
    }

}
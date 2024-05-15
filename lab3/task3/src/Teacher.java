

import java.util.List;

public class Teacher implements Runnable {

    private final String name;
    private final List<String> assignedGroups;
    private final Journal journal;
    private final int teachingWeeks;


    public Teacher(String name, List<String> assignedGroups, int teachingWeeks, Journal journal) {
        this.name = name;
        this.assignedGroups = assignedGroups;
        this.journal = journal;
        this.teachingWeeks = teachingWeeks;
    }

    @Override
    public void run() {
        for (int i = 0; i < teachingWeeks; i++) {
            for (String group : assignedGroups) {
                for (Integer studentName : this.journal.groups.get(group).groupList.keySet()) {
                    Double mark = (double) (Math.round(100 * Math.random() * 100)) / 100;
                    journal.addMark(group, studentName, mark + " (" + this.name + ")");
                }
            }
        }
    }
}
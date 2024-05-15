import java.util.Arrays;
import java.util.Collections;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Journal journal = new Journal();
        int nWeeks = 3;

        Thread t1 = new Thread(new Teacher("Lecturer 1", Arrays.asList("ІP-11", "ІP-13", "ІP-15"), nWeeks, journal));
        Thread t2 = new Thread(new Teacher("Assistant 1", Arrays.asList("ІP-11", "ІP-13", "ІP-15"), nWeeks, journal));
        Thread t3 = new Thread(new Teacher("Assistant 2", Arrays.asList("ІP-11", "ІP-13", "ІP-15"), nWeeks, journal));
        Thread t4 = new Thread(new Teacher("Assistant 3", Arrays.asList("ІP-11", "ІP-13", "ІP-15"), nWeeks, journal));

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        journal.show();
    }
}
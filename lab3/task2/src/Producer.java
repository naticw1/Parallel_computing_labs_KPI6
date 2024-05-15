

import java.util.Random;

public class Producer implements Runnable {
    private final Drop drop;

    public Producer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        int[] importantInfo = new int[1000];
        for (int i = 0; i < importantInfo.length; i++) importantInfo[i] = i + 1;

        Random random = new Random();

        for (int i : importantInfo) {
            drop.put(i);
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException ignored) {
            }
        }
        drop.put(0);
    }
}

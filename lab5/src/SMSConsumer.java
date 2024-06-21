import java.util.Random;

public class SMSConsumer extends Thread {
    private final SMSQueue q; // Reference to the SMS queue

    SMSConsumer(SMSQueue queue) {
        this.q = queue;
    }

    @Override
    public void run() {
        Random rand = new Random();

        // Process items from the queue until production is declared over
        while(!q.isOver) {
            q.getItem(); // Remove an item from the queue
            try {
                Thread.sleep(rand.nextInt(100)); // Sleep for a random time up to 100ms
            } catch (InterruptedException ignored) {}

            q.consumed++;; // Increment the count of consumed items
        }
    }
}

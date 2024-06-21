import java.util.Random;

public class SMSProducer extends Thread {
    private final SMSQueue q; // Queue manager

    SMSProducer(SMSQueue queue) {
        this.q = queue;
    }

    @Override
    public void run() {
        Random rand = new Random();
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        // Continue adding items until 10 seconds have passed
        while (elapsedTime < 10_000) {
            q.addItem(rand.nextInt(100)); // Add random item to queue

            try {
                Thread.sleep(rand.nextInt(15)); // Sleep for up to 15 milliseconds
            } catch (InterruptedException ignored) {}

            elapsedTime = System.currentTimeMillis() - startTime;
        }

        q.isOver = true; // Signal that production is over
    }
}

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SMS implements Callable<double[]> {
    private int qSize; // Queue size
    private SMSQueue q; // SMS Queue instance

    SMS(int qSize) {
        this.qSize = qSize;
        q = new SMSQueue(qSize);
    }

    public double[] call() {
        SMSProducer prodThread = new SMSProducer(q); // Producer thread
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Executor service to manage threads

        // Create consumer threads and execute them
        for (int i = 0; i < 5; i++) {
            Thread consThread = new SMSConsumer(q);
            exec.execute(consThread);
        }

        // Monitor thread to output queue status
        Thread monitor = new Thread(new Runnable() {
            public void run() {
                while(!q.isOver) {
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                    System.out.println("Queue length: " + q.queueLength() + ", rejected percentage: " + q.calculateRejectedPercentage());
                }
            }});
        exec.execute(monitor);

        exec.execute(prodThread); // Execute producer thread
        exec.shutdown(); // Shutdown executor service

        try {
            exec.awaitTermination(30, TimeUnit.SECONDS); // Await termination of all tasks
        } catch (InterruptedException e) {}

        return new double[] {q.calculateRejectedPercentage(), q.calculateAverageQueueSize()}; // Return calculation results
    }
}

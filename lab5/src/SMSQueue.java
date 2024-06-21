import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SMSQueue {
    private final int qSize; // Maximum queue size
    private int rejected; // Count of rejected items
    private final Queue<Integer> queue; // Queue storing integers
    private List<Integer> sizeMetrics; // Measures of queue size over time

    public int consumed; // Count of consumed items
    public boolean isOver; // Flag to indicate if queue processing should stop

    public synchronized int queueLength() {
        return this.queue.size(); // Return current size of the queue
    }

    SMSQueue(int qSize) {
        this.consumed = 0;
        this.rejected = 0;
        this.isOver = false;
        this.queue = new ArrayDeque<>();
        this.qSize = qSize;
        sizeMetrics = new ArrayList<>();
    }

    public synchronized void addItem(int item) {
        if(this.queue.size() >= this.qSize) {
            this.rejected += 1; // Increment rejected count if queue is full
            return;
        }

        this.queue.add(item); // Add item to the queue
        notifyAll(); // Notify waiting threads
    }

    public synchronized int getItem() {
        sizeMetrics.add(this.queue.size()); // Record queue size
        while(this.queue.size() == 0) {
            try {
                wait(); // Wait if the queue is empty
            } catch (InterruptedException ignored) {}
        }
        return this.queue.poll(); // Return and remove the front item from the queue
    }

    public double calculateRejectedPercentage() {
        return (double) this.rejected / (this.rejected + this.consumed); // Calculate percentage of rejected items
    }

    public double calculateAverageQueueSize() {
        int sum = 0;
        for (int size : sizeMetrics) {
            sum += size; // Sum of all recorded queue sizes
        }
        return (double) sum / sizeMetrics.size(); // Average queue size
    }
}

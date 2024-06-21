import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        double[] result; // Array to store results (rejected percentage, average queue size)
        SMS task; // Single SMS task instance

        List<Future<double[]>> results; // List to store results from multiple tasks
        List<Callable<double[]>> tasks; // List of tasks for the executor

        // Run a single SMS task and print results
        System.out.println("Single task results:");
        task = new SMS(100);
        result = task.call();
        System.out.println(result[0]); // Print rejected percentage
        System.out.println(result[1]); // Print average queue size

        // Executor for running multiple SMS tasks concurrently
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new SMS(100));
        }
        results = executor.invokeAll(tasks);
        executor.shutdown();

        // Calculate and print aggregated results from parallel runs
        double avgQueueSize = 0;
        double totalFailures = 0;
        for(Future<double[]> taskResult : results) {
            result = taskResult.get();
            avgQueueSize += result[1]; // Sum of average queue sizes
            totalFailures += result[0]; // Sum of failures (rejected percentages)
        }
        System.out.println("Parallel runs results:");
        System.out.println(totalFailures / results.size()); // Average failure rate
        System.out.println(avgQueueSize / results.size()); // Average queue size across tasks
    }
}

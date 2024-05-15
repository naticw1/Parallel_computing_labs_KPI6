import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        executeSimpleRun(false);
        //executeThreadNExperiment();
        //executeSizeMatrixExperiment();
    }

    public static void executeSimpleRun(boolean printMatrices) {
        int axis0Size = 2000;
        int axis1Size = 2000;

        Matrix matrixA = new Matrix(axis0Size, axis1Size);
        Matrix matrixB = new Matrix(axis0Size, axis1Size);

        matrixA.generateRandomMatrix();
        matrixB.generateRandomMatrix();

        int nThreads = Runtime.getRuntime().availableProcessors();

        Basic basicAlgorithm = new Basic(matrixA, matrixB);
        Striped stripedAlgorithm = new Striped(matrixA, matrixB, nThreads);
        Fox foxAlgorithm = new Fox(matrixA, matrixB, nThreads);

        long startTime = System.nanoTime();
        Matrix resultC = basicAlgorithm.multiply();
        long endTime = System.nanoTime() - startTime;

        if (printMatrices) resultC.print();

        System.out.println("Time for Basic Algorithm: " + endTime / 1_000_000);

        startTime = System.nanoTime();
        resultC = stripedAlgorithm.multiply();
        endTime = System.nanoTime() - startTime;

        if (printMatrices) resultC.print();

        System.out.println("Time for Striped Algorithm: " + endTime / 1_000_000);

        startTime = System.nanoTime();
        resultC = foxAlgorithm.multiply();
        endTime = System.nanoTime() - startTime;

        if (printMatrices) resultC.print();

        System.out.println("Time for Fox Algorithm: " + endTime / 1_000_000);
        System.out.println("\n");
    }

    public static void executeThreadNExperiment() {
        int axis0Size = 1000;
        int axis1Size = 1000;
        int nExperiments = 3;

        int[] stripedThreads = new int[] {2, 4, 8, 12, 16, 32, 64};
        int[] foxThreads = new int[] {2, 4, 8, 12, 16, 32, 64};
        Map<Integer, Long> stripedTimes = new HashMap<>();
        Map<Integer, Long> foxTimes = new HashMap<>();

        Matrix matrixA = new Matrix(axis0Size, axis1Size);
        Matrix matrixB = new Matrix(axis0Size, axis1Size);

        matrixA.generateRandomMatrix();
        matrixB.generateRandomMatrix();

        for (int numThreads : stripedThreads) {
            Striped stripedAlgorithm = new Striped(matrixA, matrixB, numThreads);

            long totalTime = 0;
            for (int i = 0; i < nExperiments; i++) {
                long startTime = System.nanoTime();
                Matrix resultC = stripedAlgorithm.multiply();
                totalTime += System.nanoTime() - startTime;
            }
            long averageTime = totalTime / nExperiments;

            stripedTimes.put(numThreads, averageTime / 1_000_000);
        }

        for (int numThreads : foxThreads) {
            Fox foxAlgorithm = new Fox(matrixA, matrixB, numThreads);

            long totalTime = 0;
            for (int i = 0; i < nExperiments; i++) {
                long startTime = System.nanoTime();
                Matrix resultC = foxAlgorithm.multiply();
                totalTime += System.nanoTime() - startTime;
            }
            long averageTime = totalTime / nExperiments;

            foxTimes.put(numThreads, averageTime / 1_000_000);
        }

        List<Integer> sortedStripedKeys = stripedTimes.keySet().stream().sorted().collect(Collectors.toList());

        System.out.println("**Number of threads experiment (Striped)**");

        System.out.printf("%30s", "Number of threads:");
        for (int key : sortedStripedKeys) {
            System.out.printf("%10d", key);
        }

        System.out.println();

        System.out.printf("%30s", "Time:");
        for (int key : sortedStripedKeys) {
            System.out.printf("%10d", stripedTimes.get(key));
        }
        System.out.println("\n");

        List<Integer> sortedFoxKeys = foxTimes.keySet().stream().sorted().collect(Collectors.toList());

        System.out.println("**Number of threads experiment (Fox)**");

        System.out.printf("%30s", "Number of threads:");
        for (int key : sortedFoxKeys) {
            System.out.printf("%10d", key);
        }

        System.out.println();

        System.out.printf("%30s", "Time:");
        for (int key : sortedFoxKeys) {
            System.out.printf("%10d", foxTimes.get(key));
        }
        System.out.println("\n");
    }

    public static void executeSizeMatrixExperiment() {
        int nThreads = Runtime.getRuntime().availableProcessors();
        int nExperiments = 3;

        int[] sizesArray = new int[] {10, 100, 500, 1000, 1500};
        Map<Integer, Long> stripedTimes = new HashMap<>();
        Map<Integer, Long> foxTimes = new HashMap<>();

        for (int size : sizesArray) {
            Matrix matrixA = new Matrix(size, size);
            Matrix matrixB = new Matrix(size, size);

            matrixA.generateRandomMatrix();
            matrixB.generateRandomMatrix();

            Striped stripedAlgorithm = new Striped(matrixA, matrixB, nThreads);

            long totalTimeStriped = 0;
            for (int i = 0; i < nExperiments; i++) {
                long startTime = System.nanoTime();
                Matrix resultC = stripedAlgorithm.multiply();
                totalTimeStriped += System.nanoTime() - startTime;
            }
            long averageTimeStriped = totalTimeStriped / nExperiments;

            stripedTimes.put(size, averageTimeStriped / 1_000_000);

            Fox foxAlgorithm = new Fox(matrixA, matrixB, nThreads);

            long totalTimeFox = 0;
            for (int i = 0; i < nExperiments; i++) {
                long startTime = System.nanoTime();
                Matrix resultC = foxAlgorithm.multiply();
                totalTimeFox += System.nanoTime() - startTime;
            }
            long averageTimeFox = totalTimeFox / nExperiments;

            foxTimes.put(size, averageTimeFox / 1_000_000);
        }

        List<Integer> sortedKeys = stripedTimes.keySet().stream().sorted().collect(Collectors.toList());

        System.out.println("--Experiment Matrix Size with " + nThreads + " threads--");

        System.out.printf("%30s", "Size of matrix:");
        for (int key : sortedKeys) {
            System.out.printf("%10d", key);
        }

        System.out.println();

        System.out.printf("%30s", "Time for Striped:");
        for (int key : sortedKeys) {
            System.out.printf("%10d", stripedTimes.get(key));
        }

        System.out.println();

        System.out.printf("%30s", "Time for Fox:");
        for (int key : sortedKeys) {
            System.out.printf("%10d", foxTimes.get(key));
        }

        System.out.println("\n");
    }
}

import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    static Stopwatch timer;

    public static void main(String[] args) {
        int[][] matrixA = new int[1000][1000];
        int[][] matrixB = new int[1000][1000];
        int[][] resultMatrix = new int[matrixA.length][matrixB[0].length];

        fillMatrixRandomly(matrixA);
        fillMatrixRandomly(matrixB);

        timer = Stopwatch.createStarted();
        for (int i = 0; i < 5; i++) {
            resultMatrix = multiplyMatrices(matrixA, matrixB, 12, false);
        }

        int[][] resultMatrixForkJoin = new int[matrixA.length][matrixB[0].length];

        timer = Stopwatch.createStarted();
        for (int i = 0; i < 5; i++) {
            resultMatrixForkJoin = new int[matrixA.length][matrixB[0].length];
            ParallelMatrixMultiplier.multiply(matrixA, matrixB, resultMatrixForkJoin);
        }

        System.out.println("ForkJoin Approach:");
        displayRow(resultMatrixForkJoin[0]);
        System.out.println("Thread Pool Approach:");
        displayRow(resultMatrix[0]);
    }

    static int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB, int threadCount, boolean display) {
        int[][] result = new int[matrixA.length][matrixB[0].length];
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<int[]>> results = new ArrayList<>();

        for (int row = 0; row < matrixA.length; row++) {
            MatrixRowMultiplier worker = new MatrixRowMultiplier(matrixA[row], row, matrixB);
            Future<int[]> submit = executor.submit(worker);
            results.add(submit);
        }

        for (int i = 0; i < results.size(); i++) {
            try {
                result[i] = results.get(i).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        if (display) displayRow(result);
        return result;
    }




    static void displayRow(int[] matrix) {
        for (int i = 0; i < matrix.length; i++){
            System.out.print(matrix[i] + " ");
        }
        System.out.println();
        System.out.println("Algorithm took " + timer.elapsed().toMillis() / 5 + " ms");
        try {
            Thread.sleep(new Random().nextInt(101) + 300);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void displayRow(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++){
            System.out.print(matrix[0][i] + " ");
        }
        System.out.println();
        System.out.println("Algorithm took " + timer.elapsed().toMillis() / 5 + " ms");

    }


    static void fillMatrixRandomly(int[][] matrix) {
        Random random = new Random();
        for (int[] row : matrix) {
            for (int i = 0; i < row.length; i++) {
                row[i] = random.nextInt(10); // Adjusted from 100 to 10 to match CODE2
            }
        }
    }
}

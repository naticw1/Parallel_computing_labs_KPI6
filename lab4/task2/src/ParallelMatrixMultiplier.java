import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelMatrixMultiplier extends RecursiveAction {
    private static final int STRIPE_SIZE = 150;
    private final int[][] matrixA;
    private final int[][] matrixB;
    private final int[][] resultMatrix;
    private final int startRow;
    private final int endRow;

    public ParallelMatrixMultiplier(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int startRow, int endRow) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.resultMatrix = resultMatrix;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    // Recursive compute method to handle matrix multiplication in parallel
    @Override
    protected void compute() {
        if (endRow - startRow <= STRIPE_SIZE) {
            // Direct multiplication if the task size is small enough
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < matrixB[0].length; j++) {
                    for (int k = 0; k < matrixA[0].length; k++) {
                        resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                    }
                }
            }
        } else {
            // Splitting the task into two parts for further parallel processing
            int mid = (startRow + endRow) / 2;
            invokeAll(new ParallelMatrixMultiplier(matrixA, matrixB, resultMatrix, startRow, mid),
                    new ParallelMatrixMultiplier(matrixA, matrixB, resultMatrix, mid, endRow));
        }
    }

    // Static method to initiate matrix multiplication using ForkJoin framework
    public static void multiply(int[][] matrixA, int[][] matrixB, int[][] resultMatrix) {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new ParallelMatrixMultiplier(matrixA, matrixB, resultMatrix, 0, matrixA.length));
        pool.shutdown();
    }
}
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Fox implements Algorithm {
    private Matrix matrixA;
    private Matrix matrixB;
    private int numThreads;

    public Fox(Matrix matrixA, Matrix matrixB, int numThreads) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.numThreads = numThreads;
    }

    private int findNearestDivider(int start, int value) {
        int i = start;
        while (i > 1) {
            if (value % i == 0) break;
            if (i >= start) {
                i++;
            } else {
                i--;
            }
            if (i > Math.sqrt(value)) i = Math.min(start, value / start) - 1;
        }
        return i >= start ? i : i != 0 ? value / i : value;
    }

    @Override
    public Matrix multiply() {
        Matrix matrixC = new Matrix(matrixA.getSizeAxis0(), matrixB.getSizeAxis1());

        if (!(matrixA.getSizeAxis0() == matrixA.getSizeAxis1()
                && matrixB.getSizeAxis0() == matrixB.getSizeAxis1()
                && matrixA.getSizeAxis0() == matrixB.getSizeAxis0())) {
            try {
                throw new Exception("Matrix A and B have different dimensions!");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        this.numThreads = Math.min(this.numThreads, matrixA.getSizeAxis0());
        this.numThreads = findNearestDivider(this.numThreads, matrixA.getSizeAxis0());
        int step = matrixA.getSizeAxis0() / this.numThreads;

        ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
        ArrayList<Future> threadList = new ArrayList<>();

        int[][] matrixOfSizesI = new int[numThreads][numThreads];
        int[][] matrixOfSizesJ = new int[numThreads][numThreads];

        int stepI = 0;
        for (int i = 0; i < numThreads; i++) {
            int stepJ = 0;
            for (int j = 0; j < numThreads; j++) {
                matrixOfSizesI[i][j] = stepI;
                matrixOfSizesJ[i][j] = stepJ;
                stepJ += step;
            }
            stepI += step;
        }

        for (int l = 0; l < numThreads; l++) {
            for (int i = 0; i < numThreads; i++) {
                for (int j = 0; j < numThreads; j++) {
                    int stepI0 = matrixOfSizesI[i][j];
                    int stepJ0 = matrixOfSizesJ[i][j];

                    int stepI1 = matrixOfSizesI[i][(i + l) % numThreads];
                    int stepJ1 = matrixOfSizesJ[i][(i + l) % numThreads];

                    int stepI2 = matrixOfSizesI[(i + l) % numThreads][j];
                    int stepJ2 = matrixOfSizesJ[(i + l) % numThreads][j];

                    FoxThread foxThread =
                            new FoxThread(
                                    copyBlock(matrixA, stepI1, stepJ1, step),
                                    copyBlock(matrixB, stepI2, stepJ2, step),
                                    matrixC,
                                    stepI0,
                                    stepJ0);
                    threadList.add(executor.submit(foxThread));
                }
            }
        }

        for (Future mapFuture : threadList) {
            try {
                mapFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        return matrixC;
    }

    private Matrix copyBlock(Matrix matrix, int i, int j, int size) {
        Matrix block = new Matrix(size, size);
        for (int k = 0; k < size; k++) {
            System.arraycopy(matrix.matrix[k + i], j, block.matrix[k], 0, size);
        }
        return block;
    }
}

public class MatrixRowMultiplier implements java.util.concurrent.Callable<int[]> {
    private final int[] matrixRow;
    private int[][] matrixToMultiply;

    public MatrixRowMultiplier(int[] matrixRow, int rowIndex, int[][] matrixToMultiply) {
        this.matrixRow = matrixRow;
        this.matrixToMultiply = matrixToMultiply;
    }

    @Override
    public int[] call() {
        int[] resultRow = new int[matrixToMultiply.length];
        // Perform row multiplication
        for (int col = 0; col < matrixToMultiply.length; col++) {
            for (int rowElement = 0; rowElement < matrixRow.length; rowElement++) {
                resultRow[col] += matrixRow[rowElement] * matrixToMultiply[rowElement][col];
            }
        }
        return resultRow;
    }
}




import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Striped implements Algorithm {
    Matrix A;
    Matrix B;
    private final int nThread;

    public Striped(Matrix A, Matrix B, int nThread) {
        this.A = A;
        this.B = B;
        this.nThread = nThread;
    }

    @Override
    public Matrix multiply() {
        Matrix C = new Matrix(A.getSizeAxis0(), B.getSizeAxis1());

        C = version1(C);

        return C;
    }

    public Matrix version1(Matrix C) {
        B.transpose();

        ExecutorService executor = Executors.newFixedThreadPool(this.nThread);

        List<Future<Map<String, Number>>> list = new ArrayList<>();

        for (int j = 0; j < B.getSizeAxis0(); j++) {
            for (int i = 0; i < A.getSizeAxis0(); i++) {
                Callable<Map<String, Number>> worker =
                        new StripedCallable(A.getRow(i), i, B.getRow(j), j);
                Future<Map<String, Number>> submit = executor.submit(worker);
                list.add(submit);
            }
        }

        for (Future<Map<String, Number>> mapFuture : list) {
            try {
                Map<String, Number> res = mapFuture.get();
                C.matrix[(int) res.get("rowIndex")][(int) res.get("columnIndex")] =
                        (double) res.get("value");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        B.transpose();

        return C;
    }



}
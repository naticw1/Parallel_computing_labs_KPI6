

public class Print1 implements Runnable {
    private String text = "";
    private final int n_max = 500;

    public Print1(String text) {
        this.text = text;
    }

    @Override
    public void run() {
        for (int i = 0; i < n_max; i++) {
            System.out.print(this.text);
        }
    }
}
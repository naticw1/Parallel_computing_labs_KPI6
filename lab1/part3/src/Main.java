import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Counter counter1 = new Counter();

        Runnable task1 =
                () -> {
                    processTask1(counter1);
                };
        Thread taskThread1 = new Thread(task1);

        taskThread1.start();
        taskThread1.join();

        System.out.println("Counter for Task 1 = " + counter1.getCount());

        Counter counter2 = new Counter();

        Runnable task2 =
                () -> {
                    try {
                        processTask2(counter2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
        Thread taskThread2 = new Thread(task2);

        taskThread2.start();
        taskThread2.join();

        System.out.println("Counter for Task 2 = " + counter2.getCount());

        Counter counter3 = new Counter();

        Runnable task3 =
                () -> {
                    try {
                        processTask3(counter3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
        Thread taskThread3 = new Thread(task3);

        taskThread3.start();
        taskThread3.join();

        System.out.println("Counter for Task 3 = " + counter3.getCount());

        Counter counter4 = new Counter();

        Runnable task4 =
                () -> {
                    try {
                        processTask4(counter4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
        Thread taskThread4 = new Thread(task4);

        taskThread4.start();
        taskThread4.join();

        System.out.println("Counter for Task 4 = " + counter4.getCount());
    }

    public static void processTask1(Counter counter) {
        int nTimes = 1_000_000;

        Runnable task =
                () -> {
                    for (int i = 0; i < nTimes; i++) counter.incrementUnsynchronized();
                };
        Thread taskThread = new Thread(task);

        Runnable task2 =
                () -> {
                    for (int i = 0; i < nTimes; i++) counter.decrementUnsynchronized();
                };
        Thread taskThread2 = new Thread(task2);

        taskThread.start();
        taskThread2.start();
    }

    public static void processTask2(Counter counter) throws InterruptedException {
        int nTimes = 1_000_000;

        Runnable task =
                () -> {
                    for (int i = 0; i < nTimes; i++) counter.incrementSynchronizedMethod();
                };
        Thread taskThread = new Thread(task);

        Runnable task2 =
                () -> {
                    for (int i = 0; i < nTimes; i++) counter.decrementSynchronizedMethod();
                };
        Thread taskThread2 = new Thread(task2);

        taskThread.start();
        taskThread2.start();

        taskThread.join();
        taskThread2.join();
    }

    public static void processTask3(Counter counter) throws InterruptedException {
        int nTimes = 1_000_000;

        Runnable task =
                () -> {
                    for (int i = 0; i < nTimes; i++) counter.incrementSynchronizedBlock();
                };
        Thread taskThread = new Thread(task);

        Runnable task2 =
                () -> {
                    for (int i = 0; i < nTimes; i++) counter.decrementSynchronizedBlock();
                };
        Thread taskThread2 = new Thread(task2);

        taskThread.start();
        taskThread2.start();

        taskThread.join();
        taskThread2.join();
    }

    public static void processTask4(Counter counter) throws InterruptedException {
        int nTimes = 1_000_000;

        ReentrantLock lock = new ReentrantLock();

        Runnable task =
                () -> {
                    for (int i = 0; i < nTimes; i++) {
                        lock.lock();
                        counter.incrementUnsynchronized();
                        lock.unlock();
                    }
                };
        Thread taskThread = new Thread(task);

        Runnable task2 =
                () -> {
                    for (int i = 0; i < nTimes; i++) {
                        lock.lock();
                        counter.decrementUnsynchronized();
                        lock.unlock();
                    }
                };
        Thread taskThread2 = new Thread(task2);

        taskThread.start();
        taskThread2.start();

        taskThread.join();
        taskThread2.join();
    }
}

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;

public class Bank {
    public enum LockType {
        NONE, SYNCHRONIZED, REENTRANT_LOCK, ATOMIC
    }

    public static final int NTEST = 10000;
    private int[] accounts;
    private AtomicIntegerArray atomicAccounts;
    private long ntransacts;
    private AtomicLong atomicNtransacts;
    private final ReentrantLock lock = new ReentrantLock();
    private final LockType lockType;

    public Bank(int n, int initialBalance, LockType type) {
        this.lockType = type;
        if (type == LockType.ATOMIC) {
            atomicAccounts = new AtomicIntegerArray(new int[n]);
            for (int i = 0; i < n; i++) atomicAccounts.set(i, initialBalance);
            atomicNtransacts = new AtomicLong(0);
        } else {
            accounts = new int[n];
            for (int i = 0; i < n; i++) accounts[i] = initialBalance;
            ntransacts = 0;
        }
    }

    public void transfer(int from, int to, int amount) throws InterruptedException {
        switch (lockType) {
            case NONE:
                transferBasic(from, to, amount);
                break;
            case SYNCHRONIZED:
                transferSynchronized(from, to, amount);
                break;
            case REENTRANT_LOCK:
                transferWithLock(from, to, amount);
                break;
            case ATOMIC:
                transferAtomic(from, to, amount);
                break;
            default:
                throw new IllegalArgumentException("Unknown locking type");
        }
    }

    private void transferBasic(int from, int to, int amount) {
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        if (ntransacts % NTEST == 0) test();
    }

    private synchronized void transferSynchronized(int from, int to, int amount) {
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        if (ntransacts % NTEST == 0) test();
    }

    private void transferWithLock(int from, int to, int amount) {
        lock.lock();
        try {
            accounts[from] -= amount;
            accounts[to] += amount;
            ntransacts++;
            if (ntransacts % NTEST == 0) test();
        } finally {
            lock.unlock();
        }
    }

    private void transferAtomic(int from, int to, int amount) {
        atomicAccounts.addAndGet(from, -amount);
        atomicAccounts.addAndGet(to, amount);

        long currentTransacts = atomicNtransacts.incrementAndGet();
        if (currentTransacts % NTEST == 0) {
            test();
        }
    }


    public void test() {
        int sum = 0;
        long transactionCount;
        if (lockType == LockType.ATOMIC) {
            for (int i = 0; i < atomicAccounts.length(); i++) sum += atomicAccounts.get(i);
            transactionCount = atomicNtransacts.get();
        } else {
            for (int account : accounts) sum += account;
            transactionCount = ntransacts;
        }
        System.out.println("Transactions:" + transactionCount + " Sum: " + sum);
    }



    public int size() {
        return lockType == LockType.ATOMIC ? atomicAccounts.length() : accounts.length;
    }
}



//
//import java.util.concurrent.atomic.AtomicIntegerArray;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class Bank {
//    public static final int NTEST = 10000;
//    //private final int[] accounts;
//    private final AtomicIntegerArray accounts;
//    //private long ntransacts = 0;
//    private final AtomicLong ntransacts = new AtomicLong(0);
//    private final ReentrantLock lock = new ReentrantLock();
//
//    public Bank(int n, int initialBalance) {
//        //accounts = new int[n];
//        accounts = new AtomicIntegerArray(new int[n]);
//        int i;
//        //for (i = 0; i < accounts.length; i++) accounts[i] = initialBalance;
//        //ntransacts = 0;
//         for (i = 0; i < accounts.length(); i++) accounts.set(i, initialBalance);
//         ntransacts.set(0);
//    }
//
//    public void transfer(int from, int to, int amount) throws InterruptedException {
//    accounts.addAndGet(from, -amount);
//    accounts.addAndGet(to, amount);
//    ntransacts.incrementAndGet();
//    if (ntransacts.get() % NTEST == 0) test();
//
////        accounts[from] -= amount;
////        accounts[to] += amount;
////        ntransacts++;
////        if (ntransacts % NTEST == 0) test();
//    }
//
//  /*public void transfer(int from, int to, int amount) throws InterruptedException {
//    lock.lock();
//    accounts[from] -= amount;
//    accounts[to] += amount;
//    ntransacts++;
//    if (ntransacts % NTEST == 0) test();
//    lock.unlock();
//  }*/
//
//  /*public synchronized void transfer(int from, int to, int amount) throws InterruptedException {
//    accounts[from] -= amount;
//    accounts[to] += amount;
//    ntransacts++;
//    if (ntransacts % NTEST == 0) test();
//  }*/
//
//    public void test() {
//    AtomicInteger sum = new AtomicInteger(0);
//    for (int i = 0; i < accounts.length(); i++) sum.addAndGet(accounts.get(i));
//    System.out.println("Transactions:" + ntransacts.get() + " Sum: " + sum.get());
//
////        int sum = 0;
////        for (int account : accounts) sum += account;
////        System.out.println("Transactions:" + ntransacts + " Sum: " + sum);
//    }
//
//    public int size() {
//        //return accounts.length;
//        return accounts.length();
//    }
//}
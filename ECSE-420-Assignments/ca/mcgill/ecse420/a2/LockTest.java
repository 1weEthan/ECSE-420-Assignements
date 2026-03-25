package ca.mcgill.ecse420.a2;

public class LockTest {
    
    public static int counter = 0; 
    private static final int ITERATIONS_PER_THREAD = 999;
    
    public static void main(String[] args) throws InterruptedException {
        for (int n = 2; n <= 8; n++) {
            System.out.println("Testing with n = " + n + " threads...");
            runTest(n);
        }
    }

    private static void runTest(int n) throws InterruptedException {
        counter = 0;
        Filterlock lock = new Filterlock(n); 
        Thread[] threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            // Notice we use the inner class here
            threads[i] = new Thread(new CounterTask(i, lock, ITERATIONS_PER_THREAD));
            threads[i].start();
        }

        for (int i = 0; i < n; i++) {
            threads[i].join();
        }

        int expectedCount = n * ITERATIONS_PER_THREAD;
        if (counter == expectedCount) {
            System.out.println("  SUCCESS: Expected " + expectedCount + ", got " + counter);
        } else {
            System.out.println("  FAILED: Expected " + expectedCount + ", got " + counter);
        }
    }

    static class CounterTask implements Runnable {
        private final int threadId;
        private final Filterlock lock;
        private final int iterations;

        public CounterTask(int threadId, Filterlock lock, int iterations) {
            this.threadId = threadId;
            this.lock = lock;
            this.iterations = iterations;
        }

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                lock.lock(threadId);   
                try {
                    LockTest.counter++; 
                } finally {
                    lock.unlock(threadId);
                }
            }
        }
    }
}
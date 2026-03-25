package ca.mcgill.ecse420.a2;

class CounterTask implements Runnable {
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
                // The Critical Section
                LockTest.counter++; 
            } finally {
                lock.unlock(threadId); // exit
            }
        }
    }
}


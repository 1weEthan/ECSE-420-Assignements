package ca.mcgill.ecse420.a2;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class OvertakeTest {
    // Use CopyOnWriteArrayList or Collections.synchronizedList to avoid race conditions in the log
    public static List<String> log = new CopyOnWriteArrayList<>();

    static class OvertakeTask implements Runnable {
        int threadId;
        Filterlock lock;
        int iterations = 3; // Let threads try multiple times to see overtaking

        public OvertakeTask(int threadId, Filterlock lock) { 
            this.threadId = threadId; 
            this.lock = lock; 
        }

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                log.add("Thread " + threadId + " REQUESTED lock (Iteration " + i + ")");
                lock.lock(threadId);
                try {
                    log.add("Thread " + threadId + " ENTERED critical section");
                    
                    Thread.sleep(10); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock(threadId);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int n = 8;
        Filterlock lock = new Filterlock(n);
        Thread[] threadObjects = new Thread[n];

        for (int i = 0; i < n; i++) {
            threadObjects[i] = new Thread(new OvertakeTask(i, lock));
            threadObjects[i].start();
        }
        
        for (Thread t : threadObjects) { t.join(); }

        // Analyze the logS
        for (String event : log) { System.out.println(event); }
    }
}
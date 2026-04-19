package ca.mcgill.ecse420.a3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Q2test {
    public static void main(String[] args) throws InterruptedException {
        FineGrainedList<Integer> list = new FineGrainedList<>();
        int threadCount = 10;
        int operationsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Pre-fill the list
        list.add(10);
        list.add(20);
        list.add(30);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // Wait for the signal to start
                    for (int j = 0; j < operationsPerThread; j++) {
                        // Mix of reads and potential structural changes
                        list.contains(20); 
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        long startTime = System.currentTimeMillis();
        latch.countDown(); // Start all threads simultaneously
        executor.shutdown();
        
        while (!executor.isTerminated()) {
            // Waiting for completion
        }
        
        System.out.println("Test completed in: " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("Result for 20: " + list.contains(20));
    }
}
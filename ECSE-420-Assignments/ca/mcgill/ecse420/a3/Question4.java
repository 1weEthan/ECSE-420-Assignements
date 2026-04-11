package ca.mcgill.ecse420.a3;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Question4 {

    private static final int MINIMUM = 10;
    private static final ExecutorService exec = Executors.newCachedThreadPool();
    private static final Random rand = new Random();

    // generates doubles between [-10; 10]
    public static double[][] generateRandomMatrix(int n) {
        double[][] matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = rand.nextDouble() * 20 - 10;
            }
        }

        return matrix;
    }

    public static double[] generateRandomVector(int n) {
        double[] vector = new double[n];

        for (int i = 0; i < n; i++) {
            vector[i] = rand.nextDouble() * 20 - 10;
        }

        return vector;
    }

    public static double[] multiplySequential(double[][] matrix, double[] vector) {

        int n = matrix.length;
        int m = matrix[0].length;

        if (m != vector.length) {
            throw new IllegalArgumentException("Matrix and vector size are not compatible.");
        }

        double[] result = new double[n];

        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < m; j++) {
                sum += matrix[i][j] * vector[j];
            }
            result[i] = sum;
        }

        return result;
    }

    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printVector(double[] vector) {
        for (int i = 0; i < vector.length; i++) {
            System.out.print(vector[i] + " ");
        }
        System.out.println();
    }

    public static boolean compareVectors(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            return false;
        }

        for (int i = 0; i < v1.length; i++) {
            if (Math.abs(v1[i] - v2[i]) > 0.001) {
                return false;
            }
        }

        return true;
    }
    
    public static double[] multiplyParallel(double[][] matrix, double[] vector) throws InterruptedException, ExecutionException {

        int n = matrix.length;

        if (matrix[0].length != vector.length) {
            throw new IllegalArgumentException("Matrix and vector size mismatch");
        }

        double[] result = new double[n];

        Future<?> future = exec.submit(new MultiplyTask(matrix, vector, result, 0, n));
        future.get();

        exec.shutdown();

        return result;
    }

    static class MultiplyTask implements Runnable {

        double[][] matrix;
        double[] vector;
        double[] result;
        int start;
        int end;

        MultiplyTask(double[][] matrix, double[] vector, double[] result, int start, int end) {
            this.matrix = matrix;
            this.vector = vector;
            this.result = result;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            if (end - start <= MINIMUM) {
                // Base case: compute sequentially
                for (int i = start; i < end; i++) {
                    double sum = 0.0;
                    for (int j = 0; j < vector.length; j++) {
                        sum += matrix[i][j] * vector[j];
                    }
                    result[i] = sum;
                }
            } else {
                
                try {

                    int mid = (start + end) / 2;
                    Future<?> firstHalf = exec.submit(new MultiplyTask(matrix, vector, result, start, mid));
                    Future<?> secondHalf = exec.submit(new MultiplyTask(matrix, vector, result, mid, end));
                    firstHalf.get();
                    secondHalf.get();

                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{

        int[] sizes = new int[]{100, 500, 1000, 2000, 4000};

        for (int n: sizes) {

            System.out.println("Matrix size: " + n);

            double[][] matrix = generateRandomMatrix(n);
            double[] vector = generateRandomVector(n);

            // Sequential
            long start = System.nanoTime();
            double[] sequentialResult = multiplySequential(matrix, vector);
            long end = System.nanoTime();

            double seqTime = (end - start) / 1000000;
            System.out.println("Sequential Time: " + seqTime + " ms");

            // Parallel
            start = System.nanoTime();
            double[] parallelResult = multiplyParallel(matrix, vector);
            end = System.nanoTime();

            double parTime = (end - start) / 1000000;
            System.out.println("Parallel Time: " + parTime + " ms");

            // Speedup
            double speedup = seqTime / parTime;
            System.out.println("Speedup: " + speedup);

            if (!compareVectors(sequentialResult, parallelResult)) {
                System.out.println("Results do not match.");
            }
        }
    }
}
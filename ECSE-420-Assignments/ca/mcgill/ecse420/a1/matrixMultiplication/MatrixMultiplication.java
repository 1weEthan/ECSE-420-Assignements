package ca.mcgill.ecse420.a1.matrixMultiplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = 1; // snapdragon x plus
	private static final int MATRIX_SIZE = 2000;

	public static void main(String[] args) {
		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		//measure time of both sequential and parallel
		long startTime = System.currentTimeMillis();
		double[][] result = parallelMultiplyMatrix(a, b);
		System.out.println("Parallel time with " + NUMBER_THREADS + " threads: " + (System.currentTimeMillis() - startTime) + "ms");

		//startTime = System.currentTimeMillis();
		//result = sequentialMultiplyMatrix(a, b);
		//System.out.println("sequential time: "+ (System.currentTimeMillis() - startTime) + "ms");
	}

	
	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 *
	 *
	 * Explain how you validate the method produces the correct results.
	 * I iterated through the for loops with an example matrix to see if all the calculations were made corretly
	 * I repeated for different size of matrix
	 * */

	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
		double[][] result = new double[a.length][a.length]; //assuming square matrix
		// implement a matrix multiplication sequentially:
		if (a[0].length != b.length) {
				throw new IllegalArgumentException("Matrix lengths do not match");
		}
		for (int i = 0; i < b[0].length; i++) {
			for (int j = 0; j < a.length; j++) {
				for (int k = 0; k < a[0].length; k++) {
					result[i][j] += a[i][k] * b[k][j];
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
		double[][] result = new double[a.length][a.length]; //assuming square matrix

		// each task handles a range of rows
		int rowsPerTask = a.length / NUMBER_THREADS;
		rowsPerTask = (int)Math.floor(rowsPerTask);

		List<Callable<Object>> tasks = new ArrayList<>();

		for (int i = 0; i < NUMBER_THREADS; i++) {
			int startRow = i * rowsPerTask;
			int endRow;
			// last task picks up any remaining rows
			if (i == NUMBER_THREADS - 1) {
				// take everything until the end of the array
				endRow = a.length;
			} else {
				endRow = (i + 1) * rowsPerTask;
			}
			MatrixTask aTask = new MatrixTask(a, b, result, startRow, endRow);
			startRow = endRow;

			tasks.add(Executors.callable(aTask));
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(NUMBER_THREADS);
		try {
			threadPool.invokeAll(tasks);
			threadPool.shutdown();
			threadPool.awaitTermination(1, TimeUnit.MINUTES);
		} catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		return result;

	}
        
        /**
         * Populates a matrix of given size with randomly generated integers between 0-10.
         * @param numRows number of rows
         * @param numCols number of cols
         * @return matrix
         */
		private static double[][] generateRandomMatrix(int numRows, int numCols) {
			double matrix[][] = new double[numRows][numCols];
			for (int row = 0; row < numRows; row++) {
				for (int col = 0; col < numCols; col++) {
					matrix[row][col] = (double) ((int) (Math.random() * 10.0));
				}
			}
			return matrix;
		}
	
}

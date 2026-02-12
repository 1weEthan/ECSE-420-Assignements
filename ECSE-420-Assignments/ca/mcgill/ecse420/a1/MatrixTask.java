package ca.mcgill.ecse420.a1;


public class MatrixTask implements Runnable {

    private double[][] a;
    private double[][] b;
    private double[][] result;
    private final int startRow;
    private final int endRow;

    MatrixTask(double[][] a, double[][] b, double[][] result, int start, int end) {

        this.a = a;
        this.b = b;
        this.result = result;

        this.startRow = start;
        this.endRow = end;
    }

    @Override
    public void run() {
        multiplyMatrix();
    }
    private void multiplyMatrix() {
        int n = a.length;
        for (int i = startRow; i < endRow; i++) { // Only loop through assigned rows
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
    }
}
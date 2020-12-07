package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;


public class MultiThreadedSumMatrix implements SumMatrix {
   private final int nthread; 

    public MultiThreadedSumMatrix(final int nThread) {
        this.nthread = nThread;
    }

    public class Worker extends Thread {
        private final double[][] matrix;
        private final int startPos;
        private final int numberOfElem;
        private long total;
        public Worker(final double[][] matrix, final int startPos, final int numberOfElem) {
            this.matrix = matrix; 
            this.startPos = startPos;
            this.numberOfElem = numberOfElem;
        }
        @Override
        public void run() {
            System.out.println("Working from position " + this.startPos + " to position " + (this.startPos + this.numberOfElem - 1));
            for (int i = this.startPos; i < this.matrix.length && i < this.startPos + this.numberOfElem; i++) {
                for (final double elem : this.matrix[i]) {
                    this.total += elem;
                }
            }
        }
        public final long getTotal() {
            return this.total;
        }

    }

    @Override
    public double sum(final double[][] matrix) {
        long total = 0;
        final int size = matrix.length % nthread + matrix.length / nthread;

        final List<Worker> workers = new ArrayList<>(size);

        for (int i = 0; i < matrix.length; i += size) {
            workers.add(new Worker(matrix, i, size));
        }

        for (final Worker tmp: workers) {
            tmp.start();
        }
        for (final Worker tmp : workers) {
            try {
                tmp.join();
                total += tmp.getTotal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return total;
    }
}

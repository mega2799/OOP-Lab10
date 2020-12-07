package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class MultiThreadedSumMatrixSpeed implements SumMatrix {
   private final int nthread; 

    public MultiThreadedSumMatrixSpeed(final int nThread) {
        this.nthread = nThread;
    }
    private static class Worker extends Thread {
        private final List<Double> list;
        private final int startpos;
        private final int nelem;
        private long res;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final List<Double> list, final int startpos, final int nelem) {
            super();
            this.list = list;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            // System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < list.size() && i < startpos + nelem; i++) {
                this.res += this.list.get(i);
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public long getResult() {
            return this.res;
        }

    }
   public final List<Double> matrix2list(final double[][] matrix) {
       final List<Double> makeList = new ArrayList<>(matrix.length);
       for (final double[] i : matrix) {
           for (final double j: i) {
               makeList.add(j);
           }
       }
       return makeList;
   }
   @Override
   public double sum(final double[][] matrix) {
       long time = System.currentTimeMillis();
       // final List<Double> list = matrix2list(matrix);
       final List<Double> list = Arrays.stream(matrix).flatMapToDouble(Arrays::stream).boxed().collect(Collectors.toList());
       time = System.currentTimeMillis() - time; 
       System.out.println("TO LIST TIME " + time);
       final int size = list.size() % nthread + list.size() / nthread;
       /*
        * Build a list of workers
        */
       final List<Worker> workers = new ArrayList<>(nthread);
       for (int start = 0; start < list.size(); start += size) {
           workers.add(new Worker(list, start, size));
       }
       /*
        * Start them
        */
       for (final Worker w: workers) {
           w.start();
       }
       /*
        * Wait for every one of them to finish. This operation is _way_ better done by
        * using barriers and latches, and the whole operation would be better done with
        * futures.
        */
       long sum = 0;
       for (final Worker w: workers) {
           try {
               w.join();
               sum += w.getResult();
           } catch (InterruptedException e) {
               throw new IllegalStateException(e);
           }
       }
       /*
        * Return the sum
        */
       return sum;
   }

}

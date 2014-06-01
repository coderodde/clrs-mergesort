package net.coderodde.util;

public class Arrays {

    public static final <T extends Comparable<? super T>> 
        void sort(final T[] array) {
        sortImpl(array.clone(), 
                 array, 
                 0, 
                 array.length - 1, 
                 Runtime.getRuntime().availableProcessors());
    }
    
    private static final <T extends Comparable<? super T>> 
        void sortImpl(final T[] source, 
                      final T[] target, 
                      final int from, 
                      final int to,
                      final int threads) {
        if (from == to) {
            target[from] = source[from];
            return;
        }
        
        final int mid = (from + to) >>> 1;

        if (threads == 1) {
            sortImpl(target, source, from, mid, 1);
            sortImpl(target, source, mid + 1, to, 1);
            
            int i = from;
            int left = from;
            int right = mid + 1;
            
            final int leftStop = right;
            final int rightStop = to + 1;
            
            while (left < leftStop && right < rightStop) {
                target[i++] =
                        source[right].compareTo(source[left]) < 0 ?
                        source[right++] :
                        source[left++];
            }
            
            while (left < leftStop)   target[i++] = source[left++];
            while (right < rightStop) target[i++] = source[right++];
            
            return;
        }
        
        final int leftThreads = threads >>> 1;
        final int rightThreads = threads - leftThreads;
        
        // Do parallel sorting.
        final Thread leftSortThread =  new SorterThread<T>(target,
                                                           source,
                                                           from,
                                                           mid,
                                                           leftThreads);
                                                           
        final Thread rightSortThread = new SorterThread<T>(target,
                                                           source,
                                                           mid + 1,
                                                           to,
                                                           rightThreads);
        
        leftSortThread.start();
        rightSortThread.run();
        
        try {
            leftSortThread.join();
        } catch (final InterruptedException ie) {
            ie.printStackTrace();
        }
        
        parallelMerge(source,
                      target,
                      from,
                      mid,
                      mid + 1,
                      to,
                      from,
                      threads);
    }
        
    private static final <T extends Comparable<? super T>>
        void parallelMerge(final T[] source,
                           final T[] target,
                           int p1,
                           int r1,
                           int p2,
                           int r2,
                           int p3,
                           final int threads) {
        if (threads == 1) {
            // Once here, just do the serial merge.
            final int p1stop = r1 + 1;
            final int p2stop = r2 + 1;
            
            while (p1 < p1stop && p2 < p2stop) {
                target[p3++] = 
                        source[p2].compareTo(source[p1]) < 0 ?
                        source[p2++] :
                        source[p1++];
            }
            
            while (p1 < p1stop) target[p3++] = source[p1++];
            while (p2 < p2stop) target[p3++] = source[p2++];
            
            return;
        }
            
        int n1 = r1 - p1 + 1;
        int n2 = r2 - p2 + 1;
        
        if (n1 < n2) {
            int tmp = p1;
            p1 = p2;
            p2 = tmp;
            
            tmp = r1;
            r1 = r2;
            r2 = tmp;
            
            tmp = n1;
            n1 = n2;
            n2 = tmp;
        }
        
        if (n1 == 0) {
            return;
        }
        
        final int q1 = (p1 + r1) >>> 1;
        final int q2 = binarySearch(source[q1], source, p2, r2);
        final int q3 = p3 + (q1 - p1) + (q2 - p2);
        
        target[q3] = source[q1];
        
        final int leftThreads = threads >>> 1;
        final int rightThreads = threads - leftThreads;
        
        final Thread leftMergeThread = new MergeThread<T>(source,
                                                          target,
                                                          leftThreads,
                                                          p1,
                                                          q1 - 1,
                                                          p2,
                                                          q2 - 1,
                                                          p3);
        
        final Thread rightMergeThread = new MergeThread<T>(source,
                                                           target,
                                                           rightThreads,
                                                           q1 + 1,
                                                           r1,
                                                           q2,
                                                           r2,
                                                           q3 + 1);
        
        leftMergeThread.start();
        rightMergeThread.run();
        
        try {
            leftMergeThread.join();
        } catch (final InterruptedException ie) {
            ie.printStackTrace();
        }
    }
        
    private static final <T extends Comparable<? super T>> 
        int binarySearch(final T x, final T[] array, final int p, final int r) {
        int low = p;
        int high = Math.max(p, r + 1);
        
        while (low < high) {
            int mid = (low + high) >>> 1;
            
            if (x.compareTo(array[mid]) <= 0) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        
        return high;
    }
        
    private static final class MergeThread<T extends Comparable<? super T>> 
    extends Thread {
        private final T[] source;
        private final T[] target;
        private final int threads;
        private final int p1;
        private final int r1;
        private final int p2;
        private final int r2;
        private final int p3;
        
        MergeThread(final T[] source,
                    final T[] target,
                    final int threads,
                    final int p1,
                    final int r1,
                    final int p2,
                    final int r2,
                    final int p3) {
            this.source = source;
            this.target = target;
            this.threads = threads;
            this.p1 = p1;
            this.r1 = r1;
            this.p2 = p2;
            this.r2 = r2;
            this.p3 = p3;
        }
        
        @Override
        public void run() {
            parallelMerge(source, 
                          target, 
                          p1, 
                          r1, 
                          p2, 
                          r2, 
                          p3, 
                          threads);
        }
    }
    
    private static final class SorterThread<T extends Comparable<? super T>> 
    extends Thread {
        
        private final T[] source;
        private final T[] target;
        private final int from;
        private final int to;
        private final int threads;
        
        SorterThread(final T[] source,
                     final T[] target,
                     final int from,
                     final int to,
                     final int threads) {
            this.source = source;
            this.target = target;
            this.from = from;
            this.to = to;
            this.threads = threads;
        }
        
        @Override
        public void run() {
            sortImpl(source, 
                     target, 
                     from, 
                     to, 
                     threads);
        }
    }
}

package net.coderodde.util;

import java.util.Random;

public class Demo {
 
    public static void main(String... args) {
        final int SIZE = 5000000;
        final long SEED = System.currentTimeMillis();
        final Random r = new Random(SEED);
        final Integer[] array1 = Utils.getRandomIntegerArray(SIZE, r);
        final Integer[] array2 = array1.clone();
        final Integer[] array3 = array1.clone();
        
        System.out.println("Seed: " + SEED);
        
        long ta = System.currentTimeMillis();
        
        java.util.Arrays.sort(array1);
        
        long tb = System.currentTimeMillis();
        
        System.out.println("JDK sort: " + (tb - ta) + " ms, sorted: " +
                Utils.isSorted(array1));
        
        ////
        
        ta = System.currentTimeMillis();
        
        java.util.Arrays.parallelSort(array2);
        
        tb = System.currentTimeMillis();
        
        System.out.println("JDK parallel sort: " + (tb - ta) + " ms, sorted: " +
                Utils.isSorted(array2));
        
        ////
        
        ta = System.currentTimeMillis();
        
        Arrays.sort(array3);
        
        tb = System.currentTimeMillis();
        
        System.out.println("My sort: " + (tb - ta) + " ms, sorted: " +
                Utils.isSorted(array3));
        
        System.out.println("Arrays are equal: " + 
                (Utils.arraysEqual(array1, array2) 
                && Utils.arraysEqual(array1, array3)));
    }
}

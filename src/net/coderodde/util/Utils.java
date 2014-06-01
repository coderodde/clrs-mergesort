package net.coderodde.util;

import java.util.Random;

public class Utils {

    /**
     * Creates a random integer array.
     * 
     * @param size the size of the array.
     * @param r the pseudo-random number generator.
     * 
     * @return a random integer array. 
     */
    public static final Integer[] getRandomIntegerArray(final int size,
                                                        final Random r) {
        final Integer[] array = new Integer[size];
        
        for (int i = 0; i != size; ++i) {
            array[i] = r.nextInt(size);
        }
        
        return array;
    }
    
    /**
     * Checks whether the array is sorted.
     * 
     * @param <T> the type of the array elements, must implement 
     * <code>Comparable</code>.
     * @param array the array to check.
     * 
     * @return <code>true</code> if the input array is sorted, 
     * <code>false</code> otherwise.
     */
    public static final <T extends Comparable<? super T>> 
        boolean isSorted(final T[] array) {
            
        for (int i = 0; i < array.length - 1; ++i) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }
        
        return true;
    } 
        
    public static final boolean arraysEqual(final Object[] array1, 
                                            final Object[] array2) {
        if (array1.length != array2.length) {
            return false;
        }
        
        for (int i = 0; i < array1.length; ++i) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        
        return true;
    }
}

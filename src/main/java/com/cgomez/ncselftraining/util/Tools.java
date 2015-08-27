
package com.cgomez.ncselftraining.util;
import java.util.Random;

/**
 *
 * @author Alan Filipe
 */
public class Tools {
    
    public static void shuffle(Object[] vet, Random random){
        int t = vet.length;
        for (int i=0; i<t; i++){
            int p1 = i;
            int p2 = random.nextInt(t - i) + i;
            Object aux = vet[p1];
            vet[p1] = vet[p2];
            vet[p2] = aux;
        }
    }
    
    public static boolean contains(final Object[] array, final Object v) {
        if (v == null){
            return false;
        }
        for (final Object e: array){
            if (v.equals(e)){
                return true;
            }
        }
        return false;
    }
    
    public static double round(double v, int precision){
        double f = Math.pow(10, precision);
        return (double) Math.round(v * f) / f;
    }
}
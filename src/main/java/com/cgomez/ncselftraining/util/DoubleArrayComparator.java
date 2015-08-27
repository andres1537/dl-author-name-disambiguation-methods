
package com.cgomez.ncselftraining.util;

import java.util.Comparator;

/**
 *
 * @author Alan Filipe
 */
public class DoubleArrayComparator implements Comparator<double[]>{

    public int index;
    public int dir;

    public DoubleArrayComparator(int index, boolean asc){
        this.index = index;
        if (asc){
            dir = 1;
        } else {
            dir = -1;
        }
    }

    @Override
    public int compare(double[] o1, double[] o2) {
        double diff = dir * (o1[index] - o2[index]);
        if (diff < 0){
            return 1;
        } else if(diff > 0){
            return -1;
        } else {
            return 0;
        }
    }
}
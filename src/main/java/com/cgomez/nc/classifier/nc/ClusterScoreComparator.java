
package com.cgomez.nc.classifier.nc;

import com.cgomez.nc.classifier.Cluster;
import java.util.Comparator;

/**
 *
 * @author Alan Filipe
 */
public class ClusterScoreComparator implements Comparator<Cluster> {

    @Override
    public int compare(Cluster o1, Cluster o2) {
        double diff = o1.score - o2.score;
        if (diff < 0){
            return 1;
        } else if(diff > 0){
            return -1;
        } else {
            return 0;
        }
    }
}

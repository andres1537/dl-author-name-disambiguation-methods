
package com.cgomez.hhc.classifier;

/**
 *
 * @author Alan Filipe
 */
public class Citation {
    
    public final int id;
    public int classId;
    public int predictedCluster;
    public int year;
    public String data;
    
    public Citation(int id, int classId, int predictedCluster){
        this.id = id;
        this.classId = classId;
        this.predictedCluster = predictedCluster;
    }
    
    public Citation(int id, int classId){
        this.id = id;
        this.classId = classId;
    }
    
    public boolean correct(){
        return this.predictedCluster == this.classId;
    }
}
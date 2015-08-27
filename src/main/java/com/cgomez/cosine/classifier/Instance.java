
package com.cgomez.cosine.classifier;

/**
 *
 * @author Alan Filipe
 */
public class Instance {
    
    public int id;
    public int classId;
    public int realClassId;
    public int predictedClassId;
    public double score;
    public double confidence;
    public double delta;
    public double evidence;
    public double incorrect;
    public double hit;
    public boolean selected;
    public boolean newAuthor;
    
    public boolean correct(){
        return this.predictedClassId == this.realClassId;
    }
    
}

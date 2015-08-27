/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cgomez.sland.classifier;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Alan Filipe
 */
public abstract class Classifier {
    
    public abstract void train(String dataset) throws FileNotFoundException, IOException;
    
    public abstract Evaluate test(String dataset) throws FileNotFoundException, IOException;

}

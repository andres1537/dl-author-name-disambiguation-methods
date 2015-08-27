package com.cgomez.svm.classifier;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("4. SVM");
        System.out.println("K: 0.777 ± 0.038 :: pF1: 0.702 ± 0.070");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-c", testFile, "-t", trainFile };
        Main.main(arguments);
    }
}
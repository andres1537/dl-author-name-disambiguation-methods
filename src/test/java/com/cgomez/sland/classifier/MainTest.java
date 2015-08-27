package com.cgomez.sland.classifier;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("3. SLAND");
        System.out.println("K: 0.878 ± 0.027 :: pF1: 0.869 ± 0.034");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-c", testFile, "-t", trainFile };
        Main.main(arguments);
    }
}
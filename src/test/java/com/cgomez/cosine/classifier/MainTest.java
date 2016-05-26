package com.cgomez.cosine.classifier;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("2. COSINE");
        System.out.println("K: 0.884 ± 0.028 :: pF1: 0.898 ± 0.029");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-c", testFile, "-t", trainFile, "-e", "1" };
        Main.main(arguments);
    }
}
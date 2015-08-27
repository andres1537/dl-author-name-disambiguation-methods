package com.cgomez.nb.classifier;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("5. NB");
        System.out.println("K: 0.711 ± 0.045 :: pF1: 0.616 ± 0.080");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-c", testFile, "-t", trainFile };
        Main.main(arguments);
    }
}
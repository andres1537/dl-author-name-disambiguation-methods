package com.cgomez.lasvmdbscan.classifier;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("LASVMDBSCAN");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-c", testFile, "-t", trainFile };
        Main.main(arguments);
    }
}
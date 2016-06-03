package com.cgomez.nc.classifier;

import com.cgomez.AbstractMethod;

public class MainSelfTrainingTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("1. NC-Self-Training");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-c", testFile, "-e", "0" };
        MainSelfTraining.main(arguments);
    }
}
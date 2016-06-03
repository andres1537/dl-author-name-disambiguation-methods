package com.cgomez.nc.classifier;

import com.cgomez.AbstractMethod;

public class MainSelfTrainingTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("2. NC-Self-Training");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-c", testFile, "-e", "0", "-w", "0.5", "0.3", "0.2", "-d", "0.0", "-g", "0.2", "-p", "0.1" };
        Main.main(arguments);
    }
}
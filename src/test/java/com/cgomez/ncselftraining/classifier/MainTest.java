package com.cgomez.ncselftraining.classifier;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("NC-SELF-TRAINING");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-t", testFile };
        Main.main(arguments);
    }
}
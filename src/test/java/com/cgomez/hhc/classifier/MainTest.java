package com.cgomez.hhc.classifier;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("HHC");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-t", testFile };
        Main.main(arguments);
    }
}
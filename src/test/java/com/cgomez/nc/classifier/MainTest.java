package com.cgomez.nc.classifier;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("1. NC");
        System.out.println("K: 0.919 ± 0.020 :: pF1: 0.919 ± 0.023");
        System.out.println("----------------------------------------------");
        String[] arguments = { "-c", testFile };
        Main.main(arguments);
    }
}
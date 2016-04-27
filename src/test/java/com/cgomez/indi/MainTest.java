package com.cgomez.indi;

import com.cgomez.AbstractMethod;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) throws Exception {
        System.out.println("----------------------------------------------");
        System.out.println("iNDI");
//        System.out.println("K: 0.0 ± 0.00 :: pF1: 0.0 ± 0.00");
        System.out.println("----------------------------------------------");
        String[] arguments = { indiFile, "9999", "9999", "0.1", "0.9" };
        Main.main(arguments);
    }
}
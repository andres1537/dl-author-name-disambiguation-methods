package com.cgomez.indi;

import com.cgomez.AbstractMethod;
import com.cgomez.indi.bdbcomp.Main;

public class MainTest extends AbstractMethod {
    public static void main(String[] args) throws Exception {
        System.out.println("----------------------------------------------");
        System.out.println("1. INDi");
//        System.out.println("K: 0.0 ± 0.00 :: pF1: 0.0 ± 0.00");
        System.out.println("----------------------------------------------");
//        String[] arguments = { "src/test/resources/indi/collection", "1987", "2007", "0.1", "0.9" };
        String[] arguments = { increasesFile, "1", "6", "0.1", "0.9" };
        Main.main(arguments);
    }
}
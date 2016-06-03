package com.cgomez;

public class AllTests extends AbstractMethod {
    public static void main(String[] args) throws Exception {
	com.cgomez.indi.MainTest.main(args);
	com.cgomez.nc.classifier.MainIncrementalTest.main(args);
	com.cgomez.nc.classifier.MainTest.main(args);
        com.cgomez.cosine.classifier.MainTest.main(args);
        // com.cgomez.sland.classifier.MainTest.main(args);
        // com.cgomez.svm.classifier.MainTest.main(args);
        com.cgomez.nb.classifier.MainTest.main(args);
    }
}
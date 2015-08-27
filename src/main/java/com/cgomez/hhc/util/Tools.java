
package com.cgomez.hhc.util;

import java.util.Random;

/**
 *
 * @author Alan Filipe
 */
public class Tools {
    
    public static void shuffle(Object[] vet, Random random){
        int t = vet.length;
        for (int i=0; i<t; i++){
            int p1 = i;
            int p2 = random.nextInt(t - i) + i;
            Object aux = vet[p1];
            vet[p1] = vet[p2];
            vet[p2] = aux;
        }
    }
    
    public static boolean contains(final Object[] array, final Object v) {
        if (v == null){
            return false;
        }
        for (final Object e: array){
            if (v.equals(e)){
                return true;
            }
        }
        return false;
    }
    
    public static double round(double v, int precision){
        double f = Math.pow(10, precision);
        return (double) Math.round(v * f) / f;
    }
    
    public static String changeHTMLCodeToASC(String str) {
		int[] html = { 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202,
				203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214,
				215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226,
				227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238,
				239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250,
				251, 252, 253, 254, 255, 256 };
		char[] asc = { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'c', 'e', 'e', 'e',
				'e', 'i', 'i', 'i', 'i', 'd', 'n', 'o', 'o', 'o', 'o', 'o',
				'x', '0', 'u', 'u', 'u', 'u', 'y', 'p', 'b', 'a', 'a', 'a',
				'a', 'a', 'a', 'a', 'c', 'e', 'e', 'e', 'e', 'i', 'i', 'i',
				'i', 'a', 'n', 'o', 'o', 'o', 'o', 'o', ':', '0', 'u', 'u',
				'u', 'u', 'y', 'b', 'y', 'a' };

		if (str != null) {
			// System.out.println(str);

			for (int i = 0; i < html.length; i++) {
				str = str.replace("&#" + html[i] + ";", asc[i] + "");
			}
			str = str.replaceAll("[\\.;,:\\!,\\?\\'\\\"][ ]", " ");
			str = str.replaceAll("[\\.;,:\\!,\\?\\'\\\"]", " ");
			return str.toLowerCase();
		} else
			return str;
	}
}
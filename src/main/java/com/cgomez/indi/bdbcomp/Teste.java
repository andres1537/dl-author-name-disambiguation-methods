package com.cgomez.indi.bdbcomp;

import java.io.PrintStream;

public class Teste
{
  public static void main(String[] args)
  {
    String cluster = "dac design autom intern symposium hci human comput interact intern symposium hci human comput interact";
    String artigoNovo = "interact comput";
    
    System.out.println(Similarity.cosineDistance(Util.toArray(cluster), Util.toArray(artigoNovo)));
  }
}

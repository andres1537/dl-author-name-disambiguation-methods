package com.cgomez.indi.bdbcomp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class StopList
{
  private static Set<String> stopList = new HashSet();
  private static StopList sl;
  
  static
  {
    try
    {
      BufferedReader b = new BufferedReader(new FileReader("stoplist.txt"));
      String s;
      while ((s = b.readLine()) != null)
      {
        String s;
        stopList.add(s.split("[ |]")[0].trim());
      }
      b.close();
    }
    catch (Exception e)
    {
      System.err.println("Erro ao ler stopwords.");
    }
  }
  
  public static StopList getInstance()
  {
    if (sl == null) {
      sl = new StopList();
    }
    return sl;
  }
  
  public String removeStopWord(String str)
  {
    if (str != null)
    {
      String[] vetStr = str.split("[ \\t,.;:'\"?!\\(\\)\\-]");
      String newLine = "";
      for (int i = 0; i < vetStr.length; i++)
      {
        vetStr[i] = vetStr[i].toLowerCase().trim();
        if ((!stopList.contains(vetStr[i])) && (!vetStr[i].trim().equals(""))) {
          newLine = newLine + vetStr[i] + " ";
        }
      }
      return newLine;
    }
    return str;
  }
}

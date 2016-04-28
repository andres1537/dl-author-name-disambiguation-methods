package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;

public class BinarySearch<E extends Comparable<E>>
{
  public int search(ArrayList<E> list, E e)
  {
    return search(list, e, 0, list.size() - 1);
  }
  
  private int search(ArrayList<E> list, E value, int start, int end)
  {
    if (end == start)
    {
      if (value.compareTo((Comparable)list.get(start)) == 0) {
        return start;
      }
      return -1;
    }
    if (end < start) {
      return -1;
    }
    int middle = (end + start) / 2;
    E mElement = (Comparable)list.get(middle);
    int result = value.compareTo(mElement);
    if (result > 0) {
      return search(list, value, middle + 1, end);
    }
    if (result < 0) {
      return search(list, value, start, middle - 1);
    }
    return middle;
  }
}

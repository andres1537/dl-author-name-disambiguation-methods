package com.cgomez.ncselftraining.util;

import java.util.ArrayList;

public class BinarySearch<E extends Comparable<E>> {
	
	public BinarySearch()
	{
	}
	
	public int search(ArrayList<E> list, E e)
	{
		return this.search(list, e, 0, list.size()-1);
	}
	
	private int search(ArrayList<E> list, E value, int start, int end)
	{
		if (end == start)
		{
			if (value.compareTo(list.get(start)) == 0)
			{
				return start;
			}
			return -1;
		}
		else if (end < start)
		{
			return -1;
		}
		else
		{
			int middle = (end + start)/2;
			E mElement = list.get(middle);
			int result = value.compareTo(mElement);
			if (result > 0)
			{
				return this.search(list, value, middle+1, end);
			}
			else if (result < 0)
			{
				return this.search(list, value, start, middle-1);
			}
			return middle;
		}		
	}
}

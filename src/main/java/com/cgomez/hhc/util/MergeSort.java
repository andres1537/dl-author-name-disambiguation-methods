package com.cgomez.hhc.util;

import java.util.ArrayList;

public class MergeSort<E extends Comparable<E>> {

	public void sort(ArrayList<E> list)
	{
		this.split(list, 0, list.size()-1);
	}
	
	private void split(ArrayList<E> list, int start, int end)
	{
		if (end - start + 1 > 2)
	    {
	        int middle = (end + start)/2;
	        this.split(list, start, middle);
	        this.split(list, middle+1, end);
	        this.merge(list, start, middle, middle+1, end);
	    }
	    else if (end - start + 1 == 2)
	    {
	        if (list.get(start).compareTo(list.get(end)) == 1)
	        {
	        	this.swap(list, start, end);
	        }
	    }
	    else
	    {
	        return;
	    }
	}
	
	private void merge(ArrayList<E> list, int start1, int end1, int start2, int end2)
	{
	    int size = end2 - start1 + 1;
	    ArrayList<E> listAux = new ArrayList<E>();
	    int p1 = start1;
	    int p2 = start2;
	    
	    boolean exp1 = false;
	    boolean exp2 = false;
	    
	    int i = 0;
	    while (i < size)
	    {
	        E c1 = list.get(p1);
	        E c2 = list.get(p2);
	        if (c1.compareTo(c2) <= 0)
	        {
	            listAux.add(c1);
	            p1++;
	            if (p1 > end1)
	            {
	                exp1 = true;
	                break;
	            }
	        }
	        else
	        {
	        	listAux.add(c2);
	            p2++;
	            if (p2 > end2)
	            {
	                exp2 = true;
	                break;
	            }
	        }
	        i++;
	    }
	    i++;
	    if (exp1)
	    {
	        while (i < size)
	        {
	            listAux.add(list.get(p2));
	            p2++;
	            i++;
	        }
	    }
	    else if (exp2)
	    {
	        while (i < size)
	        {
	        	listAux.add(list.get(p1));
	            p1++;
	            i++;
	        }
	    }
	    for (int h = 0; h < size; h++)
	    {
	    	E saux = listAux.get(h);
	    	list.add(start1+h, saux);
	    	list.remove(start1+h+1);
	    }
	}
	
	private void swap(ArrayList<E> list, int p1, int p2)
	{
		E aux1 = list.get(p1);
		E aux2 = list.get(p2);
		list.set(p1, aux2);
		list.set(p2, aux1);
	}
}

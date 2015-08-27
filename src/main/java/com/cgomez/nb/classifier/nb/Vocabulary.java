package com.cgomez.nb.classifier.nb;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class Vocabulary
{
	private final TreeSet<String> vc;
	
	public Vocabulary()
	{
		this.vc = new TreeSet<>();
	}

	public boolean add(String arg0)
	{
		return this.vc.add(arg0);
	}

	public boolean addAll(Collection<? extends String> c)
	{
		return this.vc.addAll(c);
	}

	public Iterator<String> iterator()
	{
		return this.vc.iterator();
	}

	public int size()
	{
		return this.vc.size();
	}
}

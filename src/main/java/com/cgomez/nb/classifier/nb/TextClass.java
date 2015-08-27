package com.cgomez.nb.classifier.nb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TextClass
{
	private double numDocs = 0;
	private double totalPos = 0;
	private final HashMap<String, Integer> tf;
	
	public TextClass()
	{
		this.tf = new HashMap<>();
	}

	public void addDoc(List<String> words)
	{
		this.totalPos = words.size();
		for (String w: words)
		{
			int v = 0;
			if (this.tf.containsKey(w))
			{
				v = this.tf.get(w) + 1;
			}
			this.tf.put(w, v);
		}
		this.numDocs++;
	}
	
	public double prob(List<String> words, double numTotalDocs, Vocabulary vc)
	{
		double vSize = vc.size();
		double prob = this.numDocs / numTotalDocs;
		Iterator<String> it = words.iterator();
		while (it.hasNext())
		{
			String vi = it.next();
			double probaux = this.prob(vi, vSize);
			prob = prob * probaux;
		}
		return prob;
	}
	
	private double prob(String word, double vSize)
	{
		double numTimes = 0;
		if (this.tf.containsKey(word))
		{
			numTimes = this.tf.get(word);
		}
		return (numTimes + 1.0) / (this.totalPos + vSize);
	}
}

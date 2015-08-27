package com.cgomez.nb.classifier.nb;

import com.cgomez.nb.classifier.Term;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class NaiveBayes<T>
{
	private final Vocabulary vocabulary;
	private final HashMap<T, TextClass> classes;
	private double numTotalDocs = 0;

	public NaiveBayes()
	{
		this.vocabulary = new Vocabulary();
		this.classes = new HashMap<>();
	}

        public void addDoc(T classObject, LinkedList<Term> terms)
	{
            LinkedList<String> words = new LinkedList<>();
            for (Term t: terms){
                words.add(t.term);
            }
            addDoc(classObject, words);
        }
        
	public void addDoc(T classObject, List<String> words)
	{
		this.vocabulary.addAll(words);
		if (this.classes.containsKey(classObject))
		{
			TextClass tc = this.classes.get(classObject);
			tc.addDoc(words);
		} else
		{
			TextClass tc = new TextClass();
			tc.addDoc(words);
			this.classes.put(classObject, tc);
		}
		this.numTotalDocs++;
	}
        
        public Result<T> classify(LinkedList<Term> terms)
	{
            LinkedList<String> words = new LinkedList<>();
            for (Term t: terms){
                words.add(t.term);
            }
            return classify(words);
        }

	public Result<T> classify(List<String> words){
		Result<T> cr = new Result<>();
		Set<T> keySet = this.classes.keySet();
		for (T key : keySet)
		{
			TextClass tc = this.classes.get(key);
			double prob = tc.prob(words, this.numTotalDocs, vocabulary);
			cr.add(new ResultItem<>(key, prob));
		}
		
		return cr;
	}
}

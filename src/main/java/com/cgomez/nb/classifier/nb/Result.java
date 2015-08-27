package com.cgomez.nb.classifier.nb;

import com.cgomez.nb.classifier.Group;
import java.util.ArrayList;

public class Result<T>
{
	private final ArrayList<ResultItem<T>> resultItems;

	public Result()
	{
		super();
		this.resultItems = new ArrayList<>();
	}

	public Result<T> combine(ArrayList<Result<T>> results)
	{
		Result<T> cr = new Result<>();
		Result<T> cr1 = results.get(0);
		for (int i = 0; i < cr1.size(); i++)
		{
			ResultItem<T> criR1 = cr1.get(i);
			ResultItem<T> criAux = new ResultItem<>(criR1.getCClass(), criR1.getProb());
			for (int j = 1; j < results.size(); j++)
			{
				Result<T> cr2 = results.get(j);
				ResultItem<T> criR2 = cr2.get(criR1.getCClass());
				criAux.setProb(criAux.getProb() * criR2.getProb());
			}
			cr.add(criAux);
		}
		return cr;
	}

	public ResultItem<T> getBestFit()
	{
		ResultItem<T> crimax = null;
		for (int i = 0; i < this.resultItems.size(); i++)
		{
			ResultItem<T> cri = this.resultItems.get(i);
            Group g = (Group) cri.getCClass();
            Group gMax = (Group) cri.getCClass();
            //System.out.println(g.id +"\t"+ cri.getProb());
			if (crimax == null || cri.getProb() > crimax.getProb())
			{
				crimax = cri;
			}
		}
        //System.out.println("Max -> "+ crimax.getProb() +"\t"+ crimax.getCClass());
		return crimax;
	}

	public boolean add(ResultItem<T> o)
	{
		return resultItems.add(o);
	}
	
	public ResultItem<T> get(T c)
	{
		for (ResultItem<T> cri : resultItems)
		{ 
			if (cri.getCClass() == c)
			{
				return cri;
			}
		}
		return null;
	}
	
	public ResultItem<T> get(int index)
	{
		return resultItems.get(index);
	}

	public int size()
	{
		return resultItems.size();
	}
	
	public void print()
	{
		System.out.println("****************");
		for (ResultItem<T> cri : resultItems)
		{ 
			System.out.println("prob=" + cri.getProb());
		}
	}

}

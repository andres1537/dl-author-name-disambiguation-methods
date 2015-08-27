package com.cgomez.hhc.po;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

public class InitialLast extends AbstractStringMetric {

	@Override
	public String getLongDescriptionString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortDescriptionString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSimilarity(String arg0, String arg1) {
		// TODO Auto-generated method stub
		String []str1 = arg0.split(" ");
		String []str2 = arg1.split(" ");
		if (str1[0].charAt(0)==str2[0].charAt(0)) {
			if (str1[str1.length-1].equals(str2[str2.length-1]))
				return 1;
		}
		else if (str1[str1.length-1].equals(str2[str2.length-1]))
			return (float)0.5;
		return 0;
	}

	@Override
	public String getSimilarityExplained(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSimilarityTimingEstimated(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getUnNormalisedSimilarity(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}

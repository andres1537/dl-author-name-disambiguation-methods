package com.cgomez.hhc.po;

public class TermIDF implements Comparable<TermIDF> {
		
		private String term;
		private double idf;
		
		public TermIDF(String term, double idf)
		{
			super();
			this.term = term;
			this.idf = idf;
		}

		public int compareTo(TermIDF o)
		{
			if (this.idf > o.getIDF())
				return 1;
			else if(this.idf < o.getIDF())
				return -1;
			else return 0;
		}
		
		public double getIDF() {
			return idf;
		}

		public void setIDF(int idf) {
			this.idf = idf;
		}

		public String getTerm() {
			return term;
		}

		public void setTerm(String term) {
			this.term = term;
		}
		
		public String toString() {
			return "Term: "+getTerm()+"\tIDF: "+getIDF();
		}

}

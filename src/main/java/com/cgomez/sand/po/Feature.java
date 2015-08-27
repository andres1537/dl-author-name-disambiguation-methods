package com.cgomez.sand.po;

public class Feature implements Comparable<Feature> {
		
		private int pos;
		private double weigth;
		
		public Feature(int pos, double weigth)
		{
			super();
			this.pos = pos;
			this.weigth = weigth;
		}

		public int compareTo(Feature o)
		{
			if (pos > o.getPos())
				return 1;
			else
				if (pos < o.getPos())
					return -1;
				else
					return 0;
		}
		
		public double getWeight() {
			return weigth;
		}

		public void setOcurrence(double weigth) {
			this.weigth = weigth;
		}

		public int getPos() {
			return pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}
	}

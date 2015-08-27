package com.cgomez.hhc.hhc;

public class Coauthor implements Comparable{
		
		private String name;
		
		public Coauthor(String name){
			this.name = name;
		}
		
		public int compareTo(Object arg) {
			int int1 = CoauthorList.hashAllCoauthors.get(name).intValue();
			int int2 = CoauthorList.hashAllCoauthors.get((String)arg).intValue();
			if (int1 > int2)
				return 1;
			else if (int1 < int2)
				return -1;
			else
				return 0;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	
}

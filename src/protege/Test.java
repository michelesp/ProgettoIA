package protege;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
		
		 ArrayList<Info> a=new ArrayList<Info>();
	        a.add(new Info("Ventilata", "2016-12-12"));
	        a.add(new Info("Dispnoica","2016-12-03"));
	        a.add(new Info("Dispnoica","2016-11-03"));
	        a.add(new Info("Fiume", "2016-12-08"));
	        Frame f=new Frame("paziente", "person", "person", "high", a, 2);
	        
	       
		HandleProtege handle=new HandleProtege();
		handle.populate(f); 
		a.add(new Info("sdf","2016-09-01"));
		Frame fr=new Frame("torace", "parameter", "parameter", "high", a, 8);
		handle.populate(fr);
	}

}

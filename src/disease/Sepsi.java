package disease;

import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.Period;

import protege.ProtegeHandler;

public class Sepsi implements Disease {
	private ProtegeHandler protegeHandler;
	
	public Sepsi(ProtegeHandler protegeHandler) {
		this.protegeHandler = protegeHandler;
	}
	
	
	@Override
	public boolean diagnose() {
		int parameters=0;
		if(verifyHR())
			parameters++;
		if(verifyNeutrophilCount())
			parameters++;
		if(verifyTachypneaBradipnea())
			parameters++;
		if(verifyWhiteBloodCellsCount())
			parameters++;
		return parameters>=2;
	}
	
	private boolean verifyHR() {
		int i = 0, j=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:hr }")) {
			i++;
			if(Integer.parseInt(URLDecoder.decode(s))>90)
				j++;
		}
		if(j/i>=0.75)
			return true;
		return false;
	}
	
	private boolean verifyTachypneaBradipnea() {
		int age=0, i=0, j=0, k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:date_of_birth }")){
			String[] s1 = URLDecoder.decode(s).split("/");
			LocalDate dateBirth = LocalDate.of(Integer.parseInt(s1[2]), Integer.parseInt(s1[1]), Integer.parseInt(s1[0]));
			age=Period.between(dateBirth, LocalDate.now()).getYears();
		}
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:rf }"))
		{
			i++;
			k += Integer.parseInt(URLDecoder.decode(s));
			if(Integer.parseInt(URLDecoder.decode(s))>20)
				j++;
		}
		int rf = k/i;
		if(j/i>=0.75)				//tachypnea
			return true;
		return ((age<=1&&rf<30)||(age<=3&&rf<25)||(age<=12&&rf<20)||(age>12&&rf<16));	//bradipnea
	}
	
	private boolean verifyWhiteBloodCellsCount() {
		int i=0, j=0, k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:white_blood_cells }"))
		{
			i++;
			if(Float.parseFloat(URLDecoder.decode(s).split("\\*?/")[0])<4000)
				j++;
			if(Float.parseFloat(URLDecoder.decode(s).split("\\*?/")[0])>12000)
				k++;
		}
		if(j/i>=0.75 || k/i>=0.75)
			return true;
		return false;
	}
	
	private boolean verifyNeutrophilCount() {
		int i=0, j=0, k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:neutrophil }"))
		{
			i++;
			if(Float.parseFloat(URLDecoder.decode(s).split("\\^")[0])>10)
				j++;
			
		}
		if(j/i>=0.75)
			return true;
		return false;
	}
}

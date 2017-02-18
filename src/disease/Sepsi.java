package disease;

import java.io.UnsupportedEncodingException;
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
		try{
			if(getInfection().equals("false"))
				return false;
			if(verifyHR())
				parameters++;
			if(verifyNeutrophilCount())
				parameters++;
			if(verifyTachypnea() || verifyBradipnea())
				parameters++;
			if(verifyWhiteBloodCellsCount())
				parameters++;
			return parameters>=2;
		}catch (Exception e) {
			return false;
		}
	}
	
	public int getHR() throws NumberFormatException, UnsupportedEncodingException {
		int hr=0, i=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:hr }")) {
			hr+=Integer.parseInt(URLDecoder.decode(s, "UTF-8"));
			i++;
		}
		return (i>0?hr/i:0);
	}
	
	private boolean verifyHR() throws NumberFormatException, UnsupportedEncodingException {
		int i = 0, j=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:hr }")) {
			i++;
			if(Integer.parseInt(URLDecoder.decode(s, "UTF-8"))>90)
				j++;
		}
		if(i>0 && j/i>=0.75)
			return true;
		return false;
	}
		
	public boolean verifyTachypnea() throws NumberFormatException, UnsupportedEncodingException {
		int age=0, i=0, j=0, k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:date_of_birth }")){
			String[] s1 = URLDecoder.decode(s, "UTF-8").split("/");
			LocalDate dateBirth = LocalDate.of(Integer.parseInt(s1[2]), Integer.parseInt(s1[1]), Integer.parseInt(s1[0]));
			age=Period.between(dateBirth, LocalDate.now()).getYears();
		}
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:rf }"))
		{
			i++;
			k += Integer.parseInt(URLDecoder.decode(s, "UTF-8"));
			if(Integer.parseInt(URLDecoder.decode(s, "UTF-8"))>20)
				j++;
		}
		if(i==0)
			return false;
		if(j/i>=0.75)
			return true;
		return false;
	}
	
	public boolean verifyBradipnea() throws UnsupportedEncodingException {
		int age=0, i=0, k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:date_of_birth }")){
			String[] s1 = URLDecoder.decode(s, "UTF-8").split("/");
			LocalDate dateBirth = LocalDate.of(Integer.parseInt(s1[2]), Integer.parseInt(s1[1]), Integer.parseInt(s1[0]));
			age=Period.between(dateBirth, LocalDate.now()).getYears();
		}
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:rf }"))
		{
			i++;
			k += Integer.parseInt(URLDecoder.decode(s, "UTF-8"));
			if(Integer.parseInt(URLDecoder.decode(s, "UTF-8"))>20) {
			}
		}
		if(i==0)
			return false;
		int rf = k/i;
		return ((age<=1&&rf<30)||(age<=3&&rf<25)||(age<=12&&rf<20)||(age>12&&rf<16));
	}

	public int getWhiteBloodCellsCount() throws NumberFormatException, UnsupportedEncodingException {
		int m = 0, i=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:white_blood_cells }"))
		{
			i++;
			m+=Float.parseFloat(URLDecoder.decode(s, "UTF-8").split("\\*?/")[0]);
		}
		if(i==0)
			return 0;
		return m/i;
	}
	
	public boolean verifyWhiteBloodCellsCount() throws NumberFormatException, UnsupportedEncodingException {
		int i=0, j=0, k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:white_blood_cells }"))
		{
			i++;
			if(Float.parseFloat(URLDecoder.decode(s, "UTF-8").split("\\*?/")[0])<4000)
				j++;
			if(Float.parseFloat(URLDecoder.decode(s, "UTF-8").split("\\*?/")[0])>12000)
				k++;
		}
		if(i>0 && (j/i>=0.75 || k/i>=0.75))
			return true;
		return false;
	}
	
	public int getNeutrophilCount() throws NumberFormatException, UnsupportedEncodingException {
		int m=0, i=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:neutrophil }"))
		{
			i++;
			m+=Float.parseFloat(URLDecoder.decode(s, "UTF-8").split("\\^")[0]);

		}
		if(i==0)
			return 0;
		return m/i;
	}
	
	private boolean verifyNeutrophilCount() throws NumberFormatException, UnsupportedEncodingException {
		int i=0, j=0, k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:neutrophil }"))
		{
			i++;
			if(Float.parseFloat(URLDecoder.decode(s, "UTF-8").split("\\^")[0])>10)
				j++;
		}
		if(i>0 && j/i>=0.75)
			return true;
		return false;
	}
	
	public String getInfection() throws UnsupportedEncodingException 
	{
		String str ="false";
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:infection }"))
		{
			return URLDecoder.decode(s, "UTF-8");
		}
		return str;
	}
}

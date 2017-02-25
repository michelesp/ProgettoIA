package disease;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import protege.ProtegeHandler;
import util.ValueAndDateTime;
import util.ValueAndDateTimeComparator;

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

	public List<ValueAndDateTime> getHR() throws NumberFormatException, UnsupportedEncodingException {
		List<ValueAndDateTime> toReturn = new ArrayList<>();
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:hr }")) 
			toReturn.add(new ValueAndDateTime(Integer.parseInt(URLDecoder.decode(s, "UTF-8")), protegeHandler.getDateTime(s)));
		toReturn.sort(new ValueAndDateTimeComparator());
		return toReturn;
	}

	public boolean verifyHR() throws NumberFormatException, UnsupportedEncodingException {
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
			if(StringUtils.isNumeric(s))
			{
				k += Integer.parseInt(URLDecoder.decode(s, "UTF-8"));
				if(Integer.parseInt(URLDecoder.decode(s, "UTF-8"))>20)
					j++;
			}
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
			if(StringUtils.isNumeric(s))
			{
				k += Integer.parseInt(URLDecoder.decode(s, "UTF-8"));
				if(Integer.parseInt(URLDecoder.decode(s, "UTF-8"))>20) {
				}
			}
		}
		if(i==0)
			return false;
		int rf = k/i;
		return ((age<=1&&rf<30)||(age<=3&&rf<25)||(age<=12&&rf<20)||(age>12&&rf<16));
	}

	public List<ValueAndDateTime> getWhiteBloodCellsCount() throws NumberFormatException, UnsupportedEncodingException {
		//int m = 0, i=0;
		List<ValueAndDateTime> toReturn = new ArrayList<>();
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:white_blood_cells }"))
			toReturn.add(new ValueAndDateTime(Float.parseFloat(URLDecoder.decode(s, "UTF-8").split("\\*?/")[0]), protegeHandler.getDateTime(s)));
		toReturn.sort(new ValueAndDateTimeComparator());
		return toReturn;
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

	public List<ValueAndDateTime> getNeutrophilCount() throws NumberFormatException, UnsupportedEncodingException {
		List<ValueAndDateTime> toReturn = new ArrayList<>();
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:neutrophil }"))
			toReturn.add(new ValueAndDateTime(Float.parseFloat(URLDecoder.decode(s, "UTF-8").split("\\^")[0]), protegeHandler.getDateTime(s)));
		toReturn.sort(new ValueAndDateTimeComparator());
		return toReturn;
	}

	public boolean verifyNeutrophilCount() throws NumberFormatException, UnsupportedEncodingException {
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

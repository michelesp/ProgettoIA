import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.Period;

import protege.ProtegeHandler;

public class TestQuery {
	static final String FILE = "Cartella_Clinica_Trascritta.docx";
	static final String FILE_TRADOTTO = "Cartella_Clinica_Tradotta.docx";
	static final String SOURCE = "http://www.semanticweb.org/vecch/ontologies/2016/3/standard-entity-representation";
	static final String ONTOLOGY_SOURCE = "StandardEntity1.owl";
	static final String ONTOLOGY_OUTPUT = "mymodel.owl";
	
	public static void main(String[] args) throws FileNotFoundException {
		int age = 0;
		int parameters = 0;
		ProtegeHandler protegeHandler = new ProtegeHandler(SOURCE, 
				new InputStreamReader(new FileInputStream(ONTOLOGY_SOURCE)), 
				new PrintWriter("test"));
		int i = 0, j=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:hr }"))
		{
			i++;
			if(Integer.parseInt(URLDecoder.decode(s))>90)
				j++;
		}
		if(j/i>=0.75)
			parameters++;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:date_of_birth }")){
			String[] s1 = URLDecoder.decode(s).split("/");
			LocalDate dateBirth = LocalDate.of(Integer.parseInt(s1[2]), Integer.parseInt(s1[1]), Integer.parseInt(s1[0]));
			age=Period.between(dateBirth, LocalDate.now()).getYears();
		}
		i=0;j=0;
		int k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:rf }"))
		{
			i++;
			k += Integer.parseInt(URLDecoder.decode(s));
			if(Integer.parseInt(URLDecoder.decode(s))>20)
				j++;
		}
		if(j/i>=0.75)
			parameters++;
		parameters = getBradipnea(age,k/i)?parameters+1:parameters;
		i =0;
		j=0;
		k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:white_blood_cells }"))
		{
			i++;
			if(Float.parseFloat(URLDecoder.decode(s).split("\\*?/")[0])<4000)
				j++;
			if(Float.parseFloat(URLDecoder.decode(s).split("\\*?/")[0])>12000)
				k++;
		}
		if(j/i>=0.75 || k/i>=0.75)
			parameters++;
		i =0;
		j=0;
		k=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:neutrophil }"))
		{
			i++;
			if(Float.parseFloat(URLDecoder.decode(s).split("\\^")[0])>10)
				j++;
			
		}
		if(j/i>=0.75)
			parameters++;
		i=0;
		System.out.println(parameters);
		if(parameters>=2)
			System.out.println("SEPSI!!!!!");
		
	}
	private static boolean getBradipnea(int age,int rf)
	{
		return ((age<=1&&rf<30)||(age<=3&&rf<25)||(age<=12&&rf<20)||(age>12&&rf<16));
	}
}

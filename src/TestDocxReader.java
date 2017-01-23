import java.io.File;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import sourcedata.StructuredDataType;

public class TestDocxReader {
	static final String FILE = "Cartella_Clinica_Trascritta.docx";
	
	public static void main(String[] args) throws Docx4JException {
		DocxReader dr = new DocxReader(new File(FILE));
		while(dr.hasNext()){
			if(dr.getNextObjectType()==StructuredDataType.STRING)
				System.out.println(dr.getNextString());
			else if(dr.getNextObjectType()==StructuredDataType.PAIR)
				System.out.println(dr.getNextPairData());
			else dr.getNextTableData();
		}
	}

}

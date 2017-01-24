import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.xml.sax.SAXException;

import sourcedata.BloodAnalysisTable;
import sourcedata.DailyClinicDiaryTable;
import sourcedata.ExamTable;
import sourcedata.StructuredDataType;
import sourcedata.TableDataItem;
import sourcedata.TypeSampleTable;
import translation.TableTranslator;
import translation.Translator;

public class TestDocx {
	static final String FILE = "Cartella_Clinica_Trascritta.docx";
	
	public static void main(String[] args) throws Docx4JException, IOException, ParserConfigurationException, SAXException {
		DocxReader dr = new DocxReader(new File(FILE));
		DocxWriter dw = new DocxWriter("tradotto.docx");
		Translator t = new Translator();
		TableTranslator tt = new TableTranslator("it", "eng", "plain", t);
		while(dr.hasNext()){
			if(dr.getNextObjectType()==StructuredDataType.STRING)
				dw.addString(t.translatePOST(dr.getNextString(), "it", "eng", "plain"));
			else{
				List<TableDataItem> l = dr.getNextTableData();
				if(l instanceof ExamTable)
					dw.addTable(tt.translate((ExamTable)l));
				else if(l instanceof DailyClinicDiaryTable)
					dw.addTable(tt.translate((DailyClinicDiaryTable)l));
				else if(l instanceof TypeSampleTable)
					dw.addTable(tt.translate((TypeSampleTable)l));
				else if(l instanceof BloodAnalysisTable)
					dw.addTable(tt.translate((BloodAnalysisTable)l));
			}
		}
		dw.save();
	}
	
}

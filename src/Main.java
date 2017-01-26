import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.xml.sax.SAXException;

import extraction.Extractor;
import protege.Frame;
import protege.ProtegeHandler;
import sourcedata.BloodAnalysisTable;
import sourcedata.DailyClinicDiaryItem;
import sourcedata.DailyClinicDiaryTable;
import sourcedata.Exam;
import sourcedata.ExamTable;
import sourcedata.StructuredDataType;
import sourcedata.TableDataItem;
import sourcedata.TypeSampleTable;
import translation.TableTranslator;
import translation.Translator;

public class Main {

	static final String FILE = "Cartella_Clinica_Trascritta.docx";
	static final String SOURCE = "http://www.semanticweb.org/vecch/ontologies/2016/3/standard-entity-representation";
	static final String ONTOLOGY_SOURCE = "standard3.owl";
	static final String ONTOLOGY_OUTPUT = "mymodel.owl";
	
	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, Exception {
		DocxReader dr = new DocxReader(new File(FILE));
		Translator t = new Translator();
		TableTranslator tt = new TableTranslator("it", "eng", "plain", t);
		Extractor ex = new Extractor();
		ProtegeHandler protegeHandler = new ProtegeHandler(SOURCE, 
				new InputStreamReader(new FileInputStream(ONTOLOGY_SOURCE)), 
				new PrintWriter(ONTOLOGY_OUTPUT));
		while(dr.hasNext()){
			if(dr.getNextObjectType()==StructuredDataType.STRING)
				for(Frame f : ex.buildFrame(t.translatePOST(dr.getNextString(), "it", "eng", "plain"),null))
					protegeHandler.addFrame(f);
			else{
				List<TableDataItem> l = dr.getNextTableData();
				if(l instanceof ExamTable) {
					ExamTable et = tt.translate((ExamTable)l);
					for(int i=0; i<et.size(); i++) {
						Exam e = (Exam)et.get(i);
						for(String str : e.getData())
							for(Frame f : ex.buildFrame(str, null))
									protegeHandler.addFrame(f);
					}
				}
				else if(l instanceof DailyClinicDiaryTable) {
					DailyClinicDiaryTable dcdt = tt.translate((DailyClinicDiaryTable)l);
					for(int i=0; i<dcdt.size(); i++) {
						DailyClinicDiaryItem dcdi = (DailyClinicDiaryItem)dcdt.get(i);
						for(String str : dcdi.getData())
							for(Frame f : ex.buildFrame(str, null))
								protegeHandler.addFrame(f);
					}
				}
				else if(l instanceof TypeSampleTable)
					//dw.addTable(tt.translate((TypeSampleTable)l));
					;
				else if(l instanceof BloodAnalysisTable)
					//dw.addTable(tt.translate((BloodAnalysisTable)l));
					;
			}
		}
		protegeHandler.save();
	}
	
}

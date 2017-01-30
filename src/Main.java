import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extraction.Extractor;
import protege.Frame;
import protege.ProtegeHandler;
import sourcedata.BloodAnalysisResults;
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
	static final String FILE_TRADOTTO = "Cartella_Clinica_Tradotta.docx";
	static final String SOURCE = "http://www.semanticweb.org/vecch/ontologies/2016/3/standard-entity-representation";
	static final String ONTOLOGY_SOURCE = "StandardEntity1.owl";
	static final String ONTOLOGY_OUTPUT = "mymodel.owl";
	static final boolean TRADOTTA = true;

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, Exception {
		DocxReader dr = new DocxReader(new File((TRADOTTA?FILE_TRADOTTO:FILE)));
		Translator t = new Translator();
		TableTranslator tt = new TableTranslator("it", "eng", "plain", t);
		Extractor ex = new Extractor();
		LocalDateTime lastDatetime = null;
		ProtegeHandler protegeHandler = new ProtegeHandler(SOURCE, 
				new InputStreamReader(new FileInputStream(ONTOLOGY_SOURCE)), 
				new PrintWriter(ONTOLOGY_OUTPUT));
		while(dr.hasNext()){
			if(dr.getNextObjectType()==StructuredDataType.STRING){
				String str = (TRADOTTA?dr.getNextString():t.translatePOST(dr.getNextString(), "it", "eng", "plain"));
				String regex = "( )*(:)( )*";
				String[] s = str.split(regex);
				if(s.length>1){
					System.out.println(s[0]+" \t "+s[1]);
					ex.buildFrame(s[0], s[1], null);
					if(s[0].equals("acceptance")){			//ultima data e ora
						String[] datetime = s[1].split(" ");
						String[] date = datetime[0].split("/");
						String[] time = datetime[1].split(":");
						lastDatetime = LocalDateTime.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), 
								Integer.parseInt(date[0]), Integer.parseInt(time[0]), (time.length>1?Integer.parseInt(time[1]):0));
						//System.err.println("last datetime: "+lastDatetime.toString());
					}
				}
				else ex.buildFrame(null, s[0], null);
			}
			/*for(Frame f : ex.buildFrame((TRADOTTA?dr.getNextString():t.translatePOST(dr.getNextString(), "it", "eng", "plain")),null))
					protegeHandler.addFrame(f);*/
			else{
				List<TableDataItem> l = dr.getNextTableData();
				if(l instanceof ExamTable) {
					ExamTable et = (TRADOTTA?(ExamTable)l:tt.translate((ExamTable)l));
					for(int i=0; i<et.size(); i++) {
						Exam e = (Exam)et.get(i);
						for(String str : e.getData())
							ex.buildFrame(str, null);
					}
				}
				else if(l instanceof DailyClinicDiaryTable) {
					DailyClinicDiaryTable dcdt = (TRADOTTA?(DailyClinicDiaryTable)l:tt.translate((DailyClinicDiaryTable)l));
					for(int i=0; i<dcdt.size(); i++) {
						DailyClinicDiaryItem dcdi = (DailyClinicDiaryItem)dcdt.get(i);
						for(String str : dcdi.getData())
							ex.buildFrame(str, null);
					}
				}
				else if(l instanceof TypeSampleTable)
					//dw.addTable(tt.translate((TypeSampleTable)l));
					;
				else if(l instanceof BloodAnalysisTable){
					for(int i=0; i<l.size(); i++) {
						BloodAnalysisResults bar = (BloodAnalysisResults) l.get(i);
						if(bar.getExam()!=null)
							ex.buildCBCFrame(bar.getExam(), bar.getResult()+bar.getUnitsOfMisure(), lastDatetime);
					}
				}
			}
		}
		for(Frame f : ex.getFrames())
			protegeHandler.addFrame(f);
		protegeHandler.save();
	}

}

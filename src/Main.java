import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import disease.Sepsi;
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
import sourcedata.BloodGasAnalysisResult;
import sourcedata.BloodGasAnalysisTable;
import translation.TableTranslator;
import translation.Translator;

public class Main {

	static final String CONF_FILE = "conf.ini";
	static String FILE;
	static String BASE;
	static String ONTOLOGY_SOURCE;
	static String ONTOLOGY_OUTPUT;
	static boolean TRANSLATED;

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, Exception {
		loadParameters();
		DocxReader dr = new DocxReader(new File(FILE));
		Translator t = new Translator();
		TableTranslator tt = new TableTranslator("it", "eng", "plain", t);
		Extractor ex = new Extractor();
		LocalDateTime lastDatetime = null;
		ProtegeHandler protegeHandler = new ProtegeHandler(BASE, 
				new InputStreamReader(new FileInputStream(ONTOLOGY_SOURCE)), 
				new PrintWriter(ONTOLOGY_OUTPUT));
		while(dr.hasNext()){
			if(dr.getNextObjectType()==StructuredDataType.STRING) {
				String str = (TRANSLATED?dr.getNextString():t.translatePOST(dr.getNextString(), "it", "eng", "plain"));
				String regex = "( )*(:)( )*";
				String[] s = str.split(regex);
				if(s.length>1) {
					ex.buildFrame(s[0].trim().replaceAll(" ", "_"), s[1], null);
					if(s[0].equals("acceptance")){			//ultima data e ora
						String[] datetime = s[1].split(" ");
						String[] date = datetime[0].split("/");
						String[] time = datetime[1].split(":");
						lastDatetime = LocalDateTime.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), 
								Integer.parseInt(date[0]), Integer.parseInt(time[0]), (time.length>1?Integer.parseInt(time[1]):0));
					}
					else if(s[0].matches("[0-9]{2}/[0-9]{2}/[0-9]{4}[a-zA-Z0-9 ]*")){					//ultima data e ora
						String[] s1=s[0].split("/");
						lastDatetime = LocalDateTime.of(Integer.parseInt(s1[2].substring(0, 4)), Integer.parseInt(s1[1]), 
								Integer.parseInt(s1[0]), Integer.parseInt(s1[2].substring(4)), 0);
					}
				}
				else ex.buildFrame(null, s[0], null);
			}
			else{
				List<TableDataItem> l = dr.getNextTableData();
				if(l instanceof ExamTable) {
					ExamTable et = (TRANSLATED?(ExamTable)l:tt.translate((ExamTable)l));
					for(int i=0; i<et.size(); i++) {
						Exam e = (Exam)et.get(i);
						for(String str : e.getData())
							ex.buildFrame(str.trim(), (lastDatetime!=null?(LocalDateTime.of(lastDatetime.toLocalDate(), e.getLocalTime())):null));
					}
				}
				else if(l instanceof DailyClinicDiaryTable) {
					DailyClinicDiaryTable dcdt = (TRANSLATED?(DailyClinicDiaryTable)l:tt.translate((DailyClinicDiaryTable)l));
					for(int i=0; i<dcdt.size(); i++) {
						DailyClinicDiaryItem dcdi = (DailyClinicDiaryItem)dcdt.get(i);
						for(String str : dcdi.getData())
							ex.buildFrame(str.trim(), LocalDateTime.of(dcdi.getLocalDate(), dcdi.getLocalTime()));
					}
				}
				else if(l instanceof BloodGasAnalysisTable) {
					for(int i=0; i<l.size(); i++) {
						BloodGasAnalysisResult ts = (BloodGasAnalysisResult) l.get(i);
						if(!ts.getExam().trim().equals(""))
							ex.buildBloodGasAnalysisFrame(ts.getExam().trim().replaceAll(" ", "_"), ts.getParameter()+ts.getUnitsOfMisure(), lastDatetime);
					}
				}
				else if(l instanceof BloodAnalysisTable){
					for(int i=0; i<l.size(); i++) {
						BloodAnalysisResults bar = (BloodAnalysisResults) l.get(i);
						if(bar.getExam()!=null && !bar.getExam().trim().equals(""))
							ex.buildCBCFrame(bar.getExam().trim().replaceAll(" ", "_"), bar.getResult()+bar.getUnitsOfMisure(), lastDatetime);
					}
				}
			}
		}
		for(Frame f : ex.getFrames())
			protegeHandler.addFrame(f);
		protegeHandler.save();

		System.out.println("Sepsi: "+new Sepsi(protegeHandler).diagnose());

	}

	static void loadParameters() throws InvalidFileFormatException, IOException {
		Section section = new Ini(new File(CONF_FILE)).get("INIT");
		FILE = section.get("file");
		BASE = section.get("base");
		ONTOLOGY_SOURCE = section.get("ontology_source");
		ONTOLOGY_OUTPUT = section.get("ontology_output");
		TRANSLATED = Boolean.parseBoolean(section.get("translated"));
	}

}

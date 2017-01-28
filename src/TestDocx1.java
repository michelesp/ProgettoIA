import java.io.File;
import java.util.List;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import sourcedata.BloodAnalysisTable;
import sourcedata.DailyClinicDiaryItem;
import sourcedata.DailyClinicDiaryTable;
import sourcedata.Exam;
import sourcedata.ExamTable;
import sourcedata.StructuredDataType;
import sourcedata.TableDataItem;
import sourcedata.TypeSampleTable;

public class TestDocx1 {
	static final String FILE = "Cartella_Clinica_Tradotta.docx";
	//static final String FILE = "Cartella_Clinica_Trascritta.docx";
	
	public static void main(String[] args) throws Docx4JException {
		DocxReader dr = new DocxReader(new File(FILE));
		while(dr.hasNext()){
			if(dr.getNextObjectType()==StructuredDataType.STRING)
				System.out.println(dr.getNextString());
			else{
				List<TableDataItem> l = dr.getNextTableData();
				if(l instanceof ExamTable)
					print((ExamTable)l);
				else if(l instanceof DailyClinicDiaryTable)
					print((DailyClinicDiaryTable)l);
				else if(l instanceof TypeSampleTable)
					print((TypeSampleTable)l);
				else if(l instanceof BloodAnalysisTable)
					print((BloodAnalysisTable)l);
				else System.err.println("array");
			}
		}
	}

	private static void print(DailyClinicDiaryTable l) {
		for(int i=0; i<l.size(); i++){
			DailyClinicDiaryItem dcdi = (DailyClinicDiaryItem) l.get(i);
			for(String str : dcdi.getData())
				System.out.println(str);
		}
	}
	
	private static void print(BloodAnalysisTable l) {

	}

	private static void print(TypeSampleTable l) {
	
	}

	private static void print(ExamTable l) {
		for(int i=0; i<l.size(); i++){
			Exam e = (Exam) l.get(i);
			for(String str : e.getData())
				System.out.println(str);
		}
	}

}

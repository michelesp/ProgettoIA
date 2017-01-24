package translation;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import sourcedata.BloodAnalysisResults;
import sourcedata.BloodAnalysisTable;
import sourcedata.DailyClinicDiaryItem;
import sourcedata.DailyClinicDiaryTable;
import sourcedata.Exam;
import sourcedata.ExamTable;
import sourcedata.TypeSampleTable;

public class TableTranslator {
	private Translator translator;
	private String from;
	private String to;
	private String format;
	
	public TableTranslator(String from, String to, String format){
		this(from, to, format, new Translator());
	}
	public TableTranslator(String from, String to, String format, Translator translator){
		this.translator = translator;
		this.from = from;
		this.to = to;
		this.format = format;
	}
	
	
	public DailyClinicDiaryTable translate(DailyClinicDiaryTable dcdt) throws IOException, ParserConfigurationException, SAXException {
		for(int i=0; i<dcdt.size(); i++){
			DailyClinicDiaryItem dcdi = (DailyClinicDiaryItem) dcdt.get(i);
			for(int j=0; j<dcdi.getData().length; j++)
				dcdi.getData()[j] = translator.translatePOST(dcdi.getData()[j], from, to, format);
		}
		return dcdt;
	}
	
	public ExamTable translate(ExamTable et) throws IOException, ParserConfigurationException, SAXException {
		for(int i=0; i<et.size(); i++){
			Exam e = (Exam) et.get(i);
			for(int j=0; j<e.getData().length; j++)
				e.getData()[j] = translator.translatePOST(e.getData()[j], from, to, format);
		}
		return et;
	}
	
	public TypeSampleTable translate(TypeSampleTable tst) {
		
		return tst;
	}
	
	public BloodAnalysisTable translate(BloodAnalysisTable bat) throws IOException, ParserConfigurationException, SAXException {
		for(int i=0; i<bat.size(); i++){
			BloodAnalysisResults bar = (BloodAnalysisResults) bat.get(i);
			BloodAnalysisResults bar1 = new BloodAnalysisResults(
					translator.translatePOST(bar.getExam(), from, to, format),
					translator.translatePOST(bar.getResult(), from, to, format),
					translator.translatePOST(bar.getUnitsOfMisure(), from, to, format),
					translator.translatePOST(bar.getReferenceRange(), from, to, format));
			bat.set(i, bar1);
		}
		return bat;
	}
}

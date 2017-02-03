import java.io.File;
import java.math.BigInteger;

import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTTblPrBase.TblStyle;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

import sourcedata.BloodAnalysisResults;
import sourcedata.BloodAnalysisTable;
import sourcedata.DailyClinicDiaryItem;
import sourcedata.DailyClinicDiaryTable;
import sourcedata.Exam;
import sourcedata.ExamTable;
import sourcedata.BloodGasAnalysisResult;
import sourcedata.BloodGasAnalysisTable;


public class DocxWriter {
	private WordprocessingMLPackage wordMLPackage;
	private String fileName;

	public DocxWriter(String fileName) throws InvalidFormatException{
		wordMLPackage = WordprocessingMLPackage.createPackage(PageSizePaper.A4, false);
		this.fileName = fileName;
	}

	public void addString(String str) {
		MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
		Text t = new Text();
		t.setValue(str);
		R r = new R();
		r.getContent().add(t);
		P p = new P();
		p.getContent().add(r);
		mdp.addObject(p);
	}

	public void addTable(DailyClinicDiaryTable dct) {
		Tbl tbl = TblFactory.createTable(0, 3, 3000);
		for(int i=0; i<dct.size(); i++){
			DailyClinicDiaryItem dci = (DailyClinicDiaryItem) dct.get(i);
			Tr tr = new Tr();
			Tc tc1 = new Tc();
			P p1 = new P();
			R r1 = new R();
			Text t1 = new Text();
			t1.setValue(dci.getLocalDate().toString());
			r1.getContent().add(t1);
			p1.getContent().add(r1);
			tc1.getContent().add(p1);
			Tc tc2 = new Tc();
			P p2 = new P();
			R r2 = new R();
			Text t2 = new Text();
			t2.setValue(dci.getLocalTime().toString());
			r2.getContent().add(t2);
			p2.getContent().add(r2);
			tc2.getContent().add(p2);
			Tc tc3 = new Tc();
			for(String str : dci.getData()){
				P p3 = new P();
				R r3 = new R();
				Text t3 = new Text();
				t3.setValue(str);
				r3.getContent().add(t3);
				p3.getContent().add(r3);
				tc3.getContent().add(p3);
			}
			tr.getContent().add(tc1);
			tr.getContent().add(tc2);
			tr.getContent().add(tc3);
			tbl.getContent().add(tr);
		}
		wordMLPackage.getMainDocumentPart().addObject(tbl);
	}

	public void addTable(BloodAnalysisTable bat) {
		Tbl tbl = TblFactory.createTable(0, 4, 2250);
		for(int i=0; i<bat.size(); i++){
			BloodAnalysisResults bar = (BloodAnalysisResults) bat.get(i);
			Tr tr = new Tr();
			Tc tc1 = new Tc();
			P p1 = new P();
			R r1 = new R();
			Text t1 = new Text();
			t1.setValue(bar.getExam());
			r1.getContent().add(t1);
			p1.getContent().add(r1);
			tc1.getContent().add(p1);
			Tc tc2 = new Tc();
			P p2 = new P();
			R r2 = new R();
			Text t2 = new Text();
			t2.setValue(bar.getResult());
			r2.getContent().add(t2);
			p2.getContent().add(r2);
			tc2.getContent().add(p2);
			Tc tc3 = new Tc();
			P p3 = new P();
			R r3 = new R();
			Text t3 = new Text();
			t3.setValue(bar.getUnitsOfMisure());
			r3.getContent().add(t3);
			p3.getContent().add(r3);
			tc3.getContent().add(p3);
			Tc tc4 = new Tc();
			P p4 = new P();
			R r4 = new R();
			Text t4 = new Text();
			t4.setValue(bar.getReferenceRange());
			r4.getContent().add(t4);
			p4.getContent().add(r4);
			tc4.getContent().add(p4);
			tr.getContent().add(tc1);
			tr.getContent().add(tc2);
			tr.getContent().add(tc3);
			tr.getContent().add(tc4);
			tbl.getContent().add(tr);
		}
		wordMLPackage.getMainDocumentPart().addObject(tbl);
	}

	public void addTable(ExamTable et) {
		Tbl tbl = TblFactory.createTable(0, 2, 4500);
		for(int i=0; i<et.size(); i++){
			Exam e = (Exam) et.get(i);
			Tr tr = new Tr();
			Tc tc1 = new Tc();
			P p1 = new P();
			R r1 = new R();
			Text t1 = new Text();
			if(e.getLocalTime()!=null)
				t1.setValue(e.getLocalTime().toString());
			else t1.setValue("");
			r1.getContent().add(t1);
			p1.getContent().add(r1);
			tc1.getContent().add(p1);
			Tc tc2 = new Tc();
			for(String str : e.getData()){
				P p2 = new P();
				R r2 = new R();
				Text t2 = new Text();
				t2.setValue(str);
				r2.getContent().add(t2);
				p2.getContent().add(r2);
				tc2.getContent().add(p2);
			}
			tr.getContent().add(tc1);
			tr.getContent().add(tc2);
			tbl.getContent().add(tr);
		}
		wordMLPackage.getMainDocumentPart().addObject(tbl);
	}

	public void addTable(BloodGasAnalysisTable tst) {
		Tbl tbl = TblFactory.createTable(0, 3, 3000);
		for(int i=0; i<tst.size(); i++){
			BloodGasAnalysisResult ts = (BloodGasAnalysisResult) tst.get(i);
			Tr tr = new Tr();
			Tc tc1 = new Tc();
			P p1 = new P();
			R r1 = new R();
			Text t1 = new Text();
			t1.setValue(ts.getExam());
			r1.getContent().add(t1);
			p1.getContent().add(r1);
			tc1.getContent().add(p1);
			Tc tc2 = new Tc();
			P p2 = new P();
			R r2 = new R();
			Text t2 = new Text();
			t2.setValue(ts.getParameter());
			r2.getContent().add(t2);
			p2.getContent().add(r2);
			tc2.getContent().add(p2);
			Tc tc3 = new Tc();
			P p3 = new P();
			R r3 = new R();
			Text t3 = new Text();
			t3.setValue(ts.getUnitsOfMisure());
			r3.getContent().add(t3);
			p3.getContent().add(r3);
			tc3.getContent().add(p3);
			tr.getContent().add(tc1);
			tr.getContent().add(tc2);
			tr.getContent().add(tc3);
			tbl.getContent().add(tr);
		}
		wordMLPackage.getMainDocumentPart().addObject(tbl);
	}

	public void save() throws Docx4JException {
		wordMLPackage.save(new File(fileName));
	}

}

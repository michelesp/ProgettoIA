package docxExtractor;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

import sourcedata.BloodAnalysisResults;
import sourcedata.BloodAnalysisTable;
import sourcedata.DailyClinicDiaryItem;
import sourcedata.DailyClinicDiaryTable;
import sourcedata.Exam;
import sourcedata.ExamTable;
import sourcedata.StructuredDataType;
import sourcedata.TableDataItem;
import sourcedata.TableType;
import sourcedata.BloodGasAnalysisResult;
import sourcedata.BloodGasAnalysisTable;

import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;

public class DocxReader {
	private List<Object> documentElm;
	private int index;

	public DocxReader(File file) throws Docx4JException {
		documentElm = Docx4J.load(file).getMainDocumentPart().getContent();
		index = 0;
	}
	
	public int getSize() {
		return documentElm.size();
	}

	public boolean hasNext() {
		return index<documentElm.size();
	}

	public StructuredDataType getNextObjectType() {
		Object o = documentElm.get(index);
		if(o instanceof P){
			return StructuredDataType.STRING;
		}
		return StructuredDataType.TAB;
	}

	public String getNextString() {
		if(documentElm.get(index).getClass()!=P.class)
			throw new RuntimeException();
		P p = (P) documentElm.get(index++);
		String str = "";
		for(Object o : p.getContent()){
			if(o instanceof R) {
				R r = (R) o;
				for(Object o1 : r.getContent()){
					if(o1 instanceof JAXBElement){
						if((((JAXBElement)o1).getValue()) instanceof Text){
							str += ((Text)((JAXBElement)o1).getValue()).getValue();
						}
					}
				}
			}
			//str += " ";
		}
		return str;
	}

	public List<TableDataItem> getNextTableData() {
		Tbl tbl  = (Tbl) ((JAXBElement)documentElm.get(index++)).getValue();
		switch (getTableType(tbl)) {
		case EXAM:
			return getExamTable(tbl);
		case BLOOD_ANALYSIS:
			return getBloodAnalysisTable(tbl);
		case BLOOD_GAS_ANALYSIS:
			return getTypeSampleTable(tbl);
		case DAILY_CLINIC_DIARY:
			return getDailyClinicDiaryTable(tbl);
		}
		return null;
	}

	private List<TableDataItem> getDailyClinicDiaryTable(Tbl tbl) {
		LocalDate localDate = null;
		List<TableDataItem> table = new DailyClinicDiaryTable();
		for(int i=1; i<tbl.getContent().size(); i++){
			LocalTime localTime;
			List<String> data = new ArrayList<>();
			Tr tr = (Tr) tbl.getContent().get(i);
			Tc tc1 = (Tc) ((JAXBElement)tr.getContent().get(0)).getValue();
			P p1 = (P) tc1.getContent().get(0);
			if(p1.getContent().size()>0){
				R r1 = (R) p1.getContent().get(0);
				for(int j=0; j<r1.getContent().size(); j++){
					Text t1 = (Text) ((JAXBElement)r1.getContent().get(j)).getValue();
					String[] s1 = t1.getValue().split("/");
					localDate = LocalDate.of(Integer.parseInt(s1[2]), Integer.parseInt(s1[1]), Integer.parseInt(s1[0]));
				}
			}
			Tc tc2 = (Tc) ((JAXBElement)tr.getContent().get(1)).getValue();
			P p2 = (P) tc2.getContent().get(0);
			R r2 = (R) p2.getContent().get(0);
			Text t2 = (Text) ((JAXBElement)r2.getContent().get(0)).getValue();
			String[] s2 = t2.getValue().split("\\.");
			localTime = LocalTime.of(Integer.parseInt(s2[0]), (s2.length==2?Integer.parseInt(s2[1]):0));
			Tc tc3 = (Tc) ((JAXBElement)tr.getContent().get(2)).getValue();
			for(Object o : tc3.getContent()){
				P p3 = (P) o;
				String str = "";
				for(Object o1 : p3.getContent()){
					if(o1 instanceof R){
						R r3 = (R) o1;
						if(((JAXBElement)r3.getContent().get(0)).getValue() instanceof Text){
							Text t3 = (Text) ((JAXBElement)r3.getContent().get(0)).getValue();
							str += t3.getValue();
						}
					}
				}
				data.add(str);
			}
			table.add(new DailyClinicDiaryItem(localDate, localTime, data.toArray(new String[data.size()])));
		}
		return table;
	}

	private List<TableDataItem> getTypeSampleTable(Tbl tbl) {
		List<TableDataItem> table = new BloodGasAnalysisTable();
		int i=2;
		boolean derived = false;
		for(; i<tbl.getContent().size() && !derived; i++) {
			String[] str = new String[]{"", "", ""};
			Tr tr = (Tr) tbl.getContent().get(i);
			for(int j=0; j<tr.getContent().size(); j++){
				Tc tc = (Tc) ((JAXBElement)tr.getContent().get(j)).getValue();
				for(int k=0; k<tc.getContent().size(); k++){
					if(tc.getContent().get(k) instanceof P){
						P p = (P) tc.getContent().get(k);
						if(p.getContent().size()>0){
							for(int k1=0; k1<p.getContent().size(); k1++){
								if(p.getContent().get(k1) instanceof R){
									R r = (R) p.getContent().get(k1);
									for(int k2=0; k2<r.getContent().size(); k2++){
										if(r.getContent().get(k2) instanceof JAXBElement){
											Text t = (Text) ((JAXBElement)r.getContent().get(k2)).getValue();
											str[j] += t.getValue();
										}
									}
								}
							}
						}
						else{
							if(j==0)
								derived = true;
							str[j] = "";
						}
					}
				}
			}
			table.add(new BloodGasAnalysisResult(str[0], str[1], str[2], derived));
		}
		derived = true;
		for(; i<tbl.getContent().size(); i++) {
			String[] str = new String[]{ "", "", ""};
			Tr tr = (Tr) tbl.getContent().get(i);
			for(int j=0; j<tr.getContent().size(); j++){
				Tc tc = (Tc) ((JAXBElement)tr.getContent().get(j)).getValue();
				for(int k=0; k<tc.getContent().size(); k++){
					if(tc.getContent().get(k) instanceof P){
						P p = (P) tc.getContent().get(k);
						if(p.getContent().size()>0){
							for(int k1=0; k1<p.getContent().size(); k1++){
								if(p.getContent().get(k1) instanceof R){
									R r = (R) p.getContent().get(k1);
									for(int k2=0; k2<r.getContent().size(); k2++){
										if(r.getContent().get(k2) instanceof JAXBElement){
											Text t = (Text) ((JAXBElement)r.getContent().get(k2)).getValue();
											str[j] += t.getValue();
										}
									}
								}
							}
						}
						else str[j] = "";
					}
				}
			}
			table.add(new BloodGasAnalysisResult(str[0], str[1], str[2], derived));
		}
		return table;
	}

	private List<TableDataItem> getBloodAnalysisTable(Tbl tbl) {
		List<TableDataItem> table = new BloodAnalysisTable();
		for(int i=1; i<tbl.getContent().size(); i++){
			String[] str = new String[]{"", "", "", ""};
			float f;
			boolean altered = false;
			Tr tr = (Tr) tbl.getContent().get(i);
			for(int j=0; j<tr.getContent().size(); j++){
				Tc tc = (Tc) ((JAXBElement)tr.getContent().get(j)).getValue();
				for(int k=0; k<tc.getContent().size(); k++){
					if(tc.getContent().get(k) instanceof P){
						P p = (P) tc.getContent().get(k);
						if(p.getContent().size()>0){
							for(int k1=0; k1<p.getContent().size(); k1++){
								if(p.getContent().get(k1) instanceof R){
									R r = (R) p.getContent().get(k1);
									for(int k2 = 0; k2<r.getContent().size(); k2++){
										if(r.getContent().get(k2) instanceof JAXBElement){
											Text t = (Text) ((JAXBElement)r.getContent().get(k2)).getValue();
											str[j] += t.getValue();
										}
									}
								}
							}
						}
					}
				}
			}
			table.add(new BloodAnalysisResults(str[0], str[1], str[2], str[3]));
		}
		return table;
	}

	private List<TableDataItem> getExamTable(Tbl tbl) {
		List<TableDataItem> table = new ExamTable();
		for(int i=1; i<tbl.getContent().size()-1; i++){
			LocalTime localTime = null;
			List<String> data = new ArrayList<>();
			Tr tr = (Tr) tbl.getContent().get(i);
			Tc tc1 = (Tc) ((JAXBElement)tr.getContent().get(0)).getValue();
			P p1 = (P) tc1.getContent().get(0);
			R r1 = (R) p1.getContent().get(0);
			if(((JAXBElement)r1.getContent().get(0)).getValue() instanceof Text){
				Text t1 = (Text) ((JAXBElement)r1.getContent().get(0)).getValue();
				String[] s1 = t1.getValue().split("\\.");
				localTime = LocalTime.of(Integer.parseInt(s1[0]), (s1.length==2?Integer.parseInt(s1[1]):0));
			}
			Tc tc2 = (Tc) ((JAXBElement)tr.getContent().get(1)).getValue();
			for(Object o : tc2.getContent()){
				P p2 = (P)o;
				String str = "";
				for(Object o1 : p2.getContent()){
					if(o1 instanceof R){
						R r2 = (R) o1;
						for(Object o2 : r2.getContent()){
							if(((JAXBElement)o2).getValue() instanceof Text){
								Text t2 = (Text) ((JAXBElement)o2).getValue();
								str += t2.getValue();
							}
						}
					}
				}
				data.add(str);
			}
			table.add(new Exam(localTime, data.toArray(new String[data.size()])));
		}
		Tr tr = (Tr) tbl.getContent().get(tbl.getContent().size()-1);
		Tc tc = (Tc) ((JAXBElement)tr.getContent().get(1)).getValue();
		P p= (P) tc.getContent().get(0);
		String str = "";
		for(int i=p.getContent().size()-1; i<p.getContent().size(); i++){
			if(p.getContent().get(i) instanceof R) {
				R r = (R) p.getContent().get(i);
				Text t = (Text) ((JAXBElement)r.getContent().get(0)).getValue();
				str += t.getValue();
			}
		}
		((ExamTable)table).setMedicalDepartment(str);
		return table;
	}

	private TableType getTableType(Tbl tbl) {
		if(tbl.getContent().size()==0)
			return null;
		Tr tr = (Tr) tbl.getContent().get(0);
		switch (tr.getContent().size()) {
		case 2:
			return TableType.EXAM;
		case 4:
			return TableType.BLOOD_ANALYSIS;
		case 3:
			P p = (P) ((Tc)((JAXBElement)tr.getContent().get(0)).getValue()).getContent().get(0);
			if(p.getContent().size()==0)
				return TableType.BLOOD_GAS_ANALYSIS;
			else if(p.getContent().size()==1)
				return TableType.DAILY_CLINIC_DIARY;
			break;
		default:
			break;
		}

		return null;
	}

}

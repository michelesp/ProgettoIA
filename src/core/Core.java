package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.ini4j.InvalidFileFormatException;

import disease.Bradicardia;
import disease.Sepsi;
import disease.Tachicardia;
import docxExtractor.DocxReader;
import extraction.Extractor;
import gui.Upgradable;
import protege.Frame;
import protege.ProtegeHandler;
import sourcedata.BloodAnalysisResults;
import sourcedata.BloodAnalysisTable;
import sourcedata.BloodGasAnalysisResult;
import sourcedata.BloodGasAnalysisTable;
import sourcedata.DailyClinicDiaryItem;
import sourcedata.DailyClinicDiaryTable;
import sourcedata.Exam;
import sourcedata.ExamTable;
import sourcedata.StructuredDataType;
import sourcedata.TableDataItem;
import translation.TableTranslator;
import translation.Translator;

public class Core {
	private Translator t;
	private TableTranslator tt;
	private Extractor ex;
	private String base;
	private String ontologySource;
	private String ontologyOutput;
	private boolean translated;
	private Upgradable gui;
	private ProtegeHandler protegeHandler;

	public Core(String base, String ontologySource, String ontologyOutput, boolean translated) throws Docx4JException, InvalidFileFormatException, IOException {
		this.translated = translated;
		this.gui = null;
		t = new Translator();
		tt = new TableTranslator("it", "eng", "plain", t);
		ex = new Extractor();
		this.base = base;
		this.ontologySource = ontologySource;
		this.ontologyOutput = ontologyOutput;
	}
	
	public void setGUI(Upgradable gui) {
		this.gui = gui;
	}

	public void execute(String file) throws Exception {
		LocalDateTime lastDatetime = null;
		DocxReader dr = new DocxReader(new File(file));
		protegeHandler = new ProtegeHandler(base, 
				new InputStreamReader(new FileInputStream(ontologySource)), 
				new PrintWriter(ontologyOutput));
		ex.clear();
		int max = dr.getSize();
		int n=0;
		while(dr.hasNext()){
			n++;
			if(gui!=null)
				gui.upgradeProgress(n*100/max);
			if(dr.getNextObjectType()==StructuredDataType.STRING) {
				String str = (translated?dr.getNextString():t.translatePOST(dr.getNextString(), "it", "eng", "plain"));
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
					ExamTable et = (translated?(ExamTable)l:tt.translate((ExamTable)l));
					for(int i=0; i<et.size(); i++) {
						Exam e = (Exam)et.get(i);
						for(String str : e.getData())
							ex.buildFrame(str.trim(), (lastDatetime!=null?(LocalDateTime.of(lastDatetime.toLocalDate(), e.getLocalTime())):null));
					}
				}
				else if(l instanceof DailyClinicDiaryTable) {
					DailyClinicDiaryTable dcdt = (translated?(DailyClinicDiaryTable)l:tt.translate((DailyClinicDiaryTable)l));
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
	}

	public boolean diagnosticsSepsi() {
		return new Sepsi(protegeHandler).diagnose();
	}

	public boolean diagnosticsBradicardia() {
		return new Bradicardia(protegeHandler).diagnose();
	}

	public boolean diagnosticsTachicardia() {
		return new Tachicardia(protegeHandler).diagnose();
	}
	
	public String getDiagnosis() {
		String msg ="";
		Sepsi s = new Sepsi(protegeHandler);
		try {
			msg += "Infezione:\t\t\t\t"+s.getInfection()+"\n";
			msg += "Frequenza cardiaca:\t\t\t"+s.getHR()+"\n";
			msg += "Frequenza respiratoria (tachypnea):\t"+s.verifyTachypnea()+"\n";
			msg += "Frequenza respiratoria (bradipnea):\t"+s.verifyBradipnea()+"\n";
			msg += "Globuli bianchi:\t\t\t"+s.getWhiteBloodCellsCount()+"\n";
			msg += "Neutrofili:\t\t\t\t"+s.getNeutrophilCount()+"\n";
		} catch (NumberFormatException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return msg;
	}

}

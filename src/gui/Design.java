package gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.opendope.SmartArt.dataHierarchy.SmartArtDataHierarchy.Images;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;

import disease.Bradicardia;
import disease.Sepsi;
import disease.Tachicardia;
import docxExtractor.DocxReader;
import extraction.Extractor;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Border;
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

import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeEvent;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class Design {

	private int SIZE_W = 450;
	private int SIZE_H = 300;
	
	private JFrame frame;
	private List<JLabel> label;
	static final String CONF_FILE = "conf.ini";
	//static String FILE;
	private File file;
	static String BASE;
	static String ONTOLOGY_SOURCE;
	static String ONTOLOGY_OUTPUT;
	static boolean TRANSLATED;
	private boolean sepsi,bradic,tachic;
	private ProtegeHandler protegeHandler;
	private boolean loaded;
	private JProgressBar progressBar;
	private JPanel panel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Design window = new Design();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Design() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		sepsi = true;
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		panel = new JPanel();
		
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(progressBar, BorderLayout.SOUTH);
		
		loaded = false;
		label = new ArrayList<>();
		//  ImageIcon loading = new ImageIcon("loader.gif");
		// frame.getContentPane().add(new JLabel("loading... ", loading, JLabel.CENTER));

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("./"));
		JMenuItem mntmCaricaCartella = new JMenuItem("Carica Cartella...");
		mntmCaricaCartella.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file =fileChooser.getSelectedFile();
					ImageIcon loading = new ImageIcon("./loader.gif");
					JLabel label2 = new JLabel("loading... ", loading, JLabel.CENTER);
					progressBar.setVisible(true);
					//frame.removeAll();
					while(panel.getComponentCount()>0)
						panel.remove(0);
					panel.add(label2, BorderLayout.CENTER);
					panel.invalidate();
					frame.pack();
					frame.setSize(SIZE_W, SIZE_H);
					frame.repaint();
					System.out.println("File: " + file.getName() + ".");  
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								loaded = false;
								createOntology(file);
								loaded = true;
								label2.setVisible(false);
								//label.get(0).setText("Caricamento completato");
								//label.get(0).invalidate();
								diagnose();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});


		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		mnNewMenu.add(mntmCaricaCartella);
		//mnNewMenu.add(fileChooser);

		/*JMenu mnNewMenu_1 = new JMenu("Malattie");
		menuBar.add(mnNewMenu_1);

		JMenu mnScegliMalattie = new JMenu("Scegli malattie");
		mnNewMenu_1.add(mnScegliMalattie);

		JCheckBoxMenuItem chckbxmntmTachicardia = new JCheckBoxMenuItem("Tachicardia");
		mnScegliMalattie.add(chckbxmntmTachicardia);
		chckbxmntmTachicardia.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				tachic = chckbxmntmTachicardia.isSelected();
			}
		});

		JCheckBoxMenuItem chckbxmntmSepsi = new JCheckBoxMenuItem("Sepsi");
		mnScegliMalattie.add(chckbxmntmSepsi);

		JCheckBoxMenuItem chckbxmntmBradicardia = new JCheckBoxMenuItem("Bradicardia");
		mnScegliMalattie.add(chckbxmntmBradicardia);

		JMenuItem mntmDiagnostica = new JMenuItem("Diagnostica");
		mntmDiagnostica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diagnose();
			}
		});
		mnNewMenu_1.add(mntmDiagnostica);
		chckbxmntmBradicardia.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				bradic = chckbxmntmBradicardia.isSelected();
			}
		});
		chckbxmntmSepsi.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				sepsi = chckbxmntmSepsi.isSelected();
			}
		});*/

		panel.add(new JLabel("Per cominciare selezionare una cartella clinica"), BorderLayout.CENTER);
	}

	private void createOntology(File file) throws IllegalAccessException, InvocationTargetException, Exception
	{
		loadParameters();
		DocxReader dr = new DocxReader(file);
		Translator t = new Translator();
		TableTranslator tt = new TableTranslator("it", "eng", "plain", t);
		Extractor ex = new Extractor();
		LocalDateTime lastDatetime = null;
		protegeHandler = new ProtegeHandler(BASE, 
				new InputStreamReader(new FileInputStream(ONTOLOGY_SOURCE)), 
				new PrintWriter(ONTOLOGY_OUTPUT));
		int max = dr.getSize();
		int n=0;
		while(dr.hasNext()){
			n++;
			progressBar.setValue(n*100/max);
			progressBar.invalidate();
			progressBar.repaint();
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

	}

	static void loadParameters() throws InvalidFileFormatException, IOException {
		Section section = new Ini(new File(CONF_FILE)).get("INIT");
		//FILE = section.get("file");
		BASE = section.get("base");
		ONTOLOGY_SOURCE = section.get("ontology_source");
		ONTOLOGY_OUTPUT = section.get("ontology_output");
		TRANSLATED = Boolean.parseBoolean(section.get("translated"));
	}
	void diagnose()
	{
		progressBar.setVisible(false);
		while(panel.getComponentCount()>0)
			panel.remove(0);
		panel.setLayout(new GridLayout(3, 1));
		label.clear();
		frame.invalidate();
		frame.repaint();
		//if(sepsi)
			panel.add(new JLabel("Sepsi: "+new Sepsi(protegeHandler).diagnose()));
		//if(bradic)
			panel.add(new JLabel("Bradicarda: "+new Bradicardia(protegeHandler).diagnose()));
		//if(tachic)
			panel.add(new JLabel("Tachicardia: "+new Tachicardia(protegeHandler).diagnose()));
		//for(int i=0; i<label.size(); i++)
		//	panel.add(label.get(i), BorderLayout.CENTER);
		panel.invalidate();
		frame.invalidate();
		frame.repaint();
		frame.pack();
		frame.setSize(SIZE_W, SIZE_H);
	}


}

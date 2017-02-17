
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import core.Core;
import gui.Design;

public class Main {

	static final String CONF_FILE = "conf.ini";
	static String FILE;
	static String BASE;
	static String ONTOLOGY_SOURCE;
	static String ONTOLOGY_OUTPUT;
	static boolean TRANSLATED;
	static boolean GUI;

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, Exception {
		loadParameters();
		Core core = new Core(BASE, ONTOLOGY_SOURCE, ONTOLOGY_OUTPUT, TRANSLATED);
		if(GUI){
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Design window = new Design(core);
						core.setGUI(window);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		else {
			core.execute(FILE);
			System.out.println("Sepsi: "+core.diagnosticsSepsi());
			System.out.println(core.getDiagnosis());
			//System.out.println("Bradicarda: "+core.diagnosticsBradicardia());
			//System.out.println("Tachicardia: "+core.diagnosticsTachicardia());
		}
	}

	static void loadParameters() throws InvalidFileFormatException, IOException {
		Section section = new Ini(new File(CONF_FILE)).get("INIT");
		FILE = section.get("file");
		BASE = section.get("base");
		ONTOLOGY_SOURCE = section.get("ontology_source");
		ONTOLOGY_OUTPUT = section.get("ontology_output");
		TRANSLATED = Boolean.parseBoolean(section.get("translated"));
		GUI = Boolean.parseBoolean(section.get("gui"));
	}

}

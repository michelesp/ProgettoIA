package util;

public class UMLStoProtegeCategotyMapping {
	
	public static String mapping(String str) {
		switch(str) {
		case "aggp":
		case "tmco":
			return "timing";
		case "npop":				//
		case "ftcn":				//
		case "grpa":				//
			return "medical";
		case "medd":
		case "inpr":				//
			return "equipment";
		case "fndg":
		case "patf":				//
			return "diagnosis";
		case "hlca":
			return "parameter";
		default:
			System.err.println("unmapped word: "+str);
		}
		return null;
	}
}

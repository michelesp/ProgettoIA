package util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

public class UMLStoProtegeCategotyMapping {
	private Map<String, String> map;

	public UMLStoProtegeCategotyMapping(File file) throws InvalidFileFormatException, IOException {
		map = new HashMap<>();
		Ini ini = new Ini(file);
		Section section = ini.get("MAPPING");
		for(String key : section.keySet())
			for(String value : section.get(key).split(";"))
				if(value.length()>0)
					map.put(value, key);
	}

	public String mapping(String str) {
		if(map.containsKey(str))
			return map.get(str);
		System.err.println("unmapped word: "+str);
		return "medic";
	}
}

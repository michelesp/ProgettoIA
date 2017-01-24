package translation;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Test {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		// TODO Auto-generated method stub
		Translator t = new Translator();
		System.out.println(t.translatePOST("Ciao come va", "it", "eng", "plain"));
	}

}

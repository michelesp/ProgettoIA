package disease;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import protege.ProtegeHandler;

public class Tachicardia implements Disease{
	ProtegeHandler protegeHandler;
	public Tachicardia(ProtegeHandler protegeHandler) {
		this.protegeHandler = protegeHandler;
	}

	@Override
	public boolean diagnose() {
		// TODO Auto-generated method stub
		try {
			return verifyTachicardia();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private boolean verifyTachicardia() throws NumberFormatException, UnsupportedEncodingException {
		int i = 0, j=0;
		for(String s : protegeHandler.querySPARQL("SELECT ?x WHERE { ?x  a uri:hr }")) {
			i++;
			if(Integer.parseInt(URLDecoder.decode(s, "UTF-8"))>=100)
				j++;
		}
		if(i>0 && j/i>=0.75)
			return true;
		return false;
	}

}

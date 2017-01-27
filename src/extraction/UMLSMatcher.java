package extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.ner.MetaMapLite;

public class UMLSMatcher {
	private MetaMapLite metaMapLiteInst;
	public UMLSMatcher()
	{
		
		Properties myProperties = MetaMapLite.getDefaultConfiguration();
		MetaMapLite.expandModelsDir(myProperties,
				"./data/models");
		MetaMapLite.expandIndexDir(myProperties,
				"./data/ivf/strict");
		myProperties.setProperty("metamaplite.excluded.termsfile",
				"./data/specialterms.txt");
		myProperties.setProperty("metamaplite.enable.postagging", "false");
		try {
			metaMapLiteInst = new MetaMapLite(myProperties);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<Entity> getEntities(String text) throws IllegalAccessException, InvocationTargetException, IOException, Exception
	{
		BioCDocument document = FreeText.instantiateBioCDocument(text);
		List<BioCDocument> documentList = new ArrayList<BioCDocument>();
		documentList.add(document);
		List<Entity> entityList = metaMapLiteInst.processDocumentList(documentList);
		return entityList;
	}
}

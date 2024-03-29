package extraction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.EntityLookup4;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.ner.MetaMapLite;

public class UMLSMatcher {
	private MetaMapLite metaMapLiteInst;
	private EntityLookup4 el;
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
			el = new EntityLookup4();
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
	public String getSemanticType(List<Entity> entityList) throws Exception
	{
		if(entityList.isEmpty())
			return "";
		if(!entityList.get(0).getEvList().isEmpty())
		{
			String cui = entityList.get(0).getEvList().get(0).getConceptInfo().getCUI();
			Set<String> set = el.getSemanticTypeSet(cui);
			String semanticType = set.toString().substring(1, set.toString().length()-1);
			return semanticType;
		}
		return "";
	}
}

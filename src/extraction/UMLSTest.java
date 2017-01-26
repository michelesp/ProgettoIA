package extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import gov.nih.nlm.nls.metamap.lite.EntityLookup4;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import translation.Translator;

public class UMLSTest {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, IOException, Exception {
		// TODO Auto-generated method stub
		String text= "patient";
		UMLSMatcher matcher = new UMLSMatcher();
		EntityLookup4 el = new EntityLookup4();
		List<Entity> entityList = matcher.getEntities(text);
		System.out.println(entityList.size());
		String cui = entityList.get(0).getEvList().get(0).getConceptInfo().getCUI();
		Set<String> set1 = el.getSemanticTypeSet(cui);
		System.out.println(set1.toString());
		for (Entity entity: entityList) {
			for (Ev ev: entity.getEvSet()) {
				System.out.print(ev.getConceptInfo().getCUI() + "|" + entity.getMatchedText());
				Set<String> set = el.getSemanticTypeSet(ev.getConceptInfo().getCUI());
				System.out.println(set.toString());
			}
		}
	}

}

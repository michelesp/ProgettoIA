package extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import gov.nih.nlm.nls.metamap.lite.EntityLookup4;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import protege.Frame;

public class Extractor {

	NLProcessor processor;
	UMLSMatcher matcher;
	public Extractor()
	{
		processor = new NLProcessor();
		matcher = new UMLSMatcher();
	}
	/*public List<Frame> buildFrames(String text) throws IllegalAccessException, InvocationTargetException, IOException, Exception
	{
		List<CoreMap> sentences = processor.getAnnotatedSentences(text);
		if (sentences != null && ! sentences.isEmpty()) {
			for(CoreMap sentence : sentences)
			{
				System.out.println("The first sentence is:");
			    System.out.println(sentence.toShorterString());
			    for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
			        String value = token.get(CoreAnnotations.ValueAnnotation.class);
			        System.out.println("Token value:"+value);
			        List<Entity> entities = matcher.getEntities(value);
			        if(entities.isEmpty())
			        	continue;
			        else
			        {
			        	
			        	Frame frame = new Frame(value,);
			        }
			        
			     }
			}
		}
		return null;
		
	}*/
	/*public List<Frame> buildFrame(String text) throws IllegalAccessException, InvocationTargetException, IOException, Exception
	{
		EntityLookup4 el = new EntityLookup4();
		List<CoreMap> sentences = processor.getAnnotatedSentences(text);
		if (sentences != null && ! sentences.isEmpty()) {
			for(CoreMap sentence : sentences)
			{
				System.out.println("The first sentence is:");
			    System.out.println(sentence.toShorterString());
			    //Da sostituire con iterazione unica sul grafo semantic con gs.vertexListSorted() che restituisceu na lista ordinata in ordine di comparsa nel testo di IndexedWords
			    for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
			        String value = token.get(CoreAnnotations.ValueAnnotation.class);
			        System.out.println("Token value:"+value);
			        List<Entity> entities = matcher.getEntities(value);
			        Set<String> set = null;
			        //for (Ev ev: entities.get(0).getEvSet()) {
					//	 set = el.getSemanticTypeSet(ev.getConceptInfo().getCUI());
					//}
			        //String semanticType = set.toString();
			        if(entities.isEmpty())
			        	continue;
			        else
			        {
			        	SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
			        	List<IndexedWord> iWords = sg.getAllNodesByWordPattern(value);
			        	//System.out.println(iWords.get(0).keySet());
			        	String pos = iWords.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);
			        	Frame frame = new Frame(value,"",pos);
			        	Set<IndexedWord> descWords = sg.descendants(iWords.get(0));
			        	for(IndexedWord desc : descWords)
			        	{
			        		String pos = desc.get(CoreAnnotations.PartOfSpeechAnnotation.class);
			        		if(!(pos.equals("RB")||pos.equals("IN")||pos.equals("DT")||pos.equals("PUNCT")))
			        		{
			        			frame.addInfo(desc.originalText(), null);
			        		}
			        	}
			        	System.out.println("Frame:"+frame);
			        }
			        
			     }
			}
		}
		return null;
	}*/
	public List<Frame> buildFrame(String text, LocalDate date) throws IllegalAccessException, InvocationTargetException, IOException, Exception
	{
		//Usare una Mappa di Term,Frame così da scorrere il testo un'unica volta
		//e aggiungere informazioni al frame precedente eventualmente
		//ci siano informazioni da aggiungere
		EntityLookup4 el = new EntityLookup4();
		//Frasi annotate dall'NLPProcessor
		List<CoreMap> sentences = processor.getAnnotatedSentences(text);
		if (sentences != null && ! sentences.isEmpty()) {
			for(CoreMap sentence : sentences)
			{
				//System.out.println("The first sentence is:");
			    //System.out.println(sentence.toShorterString());
			    //Grafo semantico con dipendenze e valori di pos 
			    SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
			    for(IndexedWord iWord: sg.vertexListSorted())
			    {
			        String value = iWord.originalText();
			        String pos = iWord.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				   //Se non è un nome o un verbo non considero la creazione del frame
			        if(!(pos.startsWith("NN")||pos.startsWith("VB")))
				    	continue;
			        List<Entity> entities = matcher.getEntities(value);
			        Set<String> set = null;
			        //for (Ev ev: entities.get(0).getEvSet()) {
					//	 set = el.getSemanticTypeSet(ev.getConceptInfo().getCUI());
					//}
			        //String semanticType = set.toString();
			        if(entities.isEmpty())
			        	continue;
			        else
			        {
			        	Collection<TypedDependency> deps = sg.typedDependencies();
			        	Frame frame = new Frame(value,"",pos);
			        	//Prendo quelle che io SUPPONGO siano le dipendenze grammaticali che dobbiamo considerare
			        	//Potrei accedervi prendendo dalla parola tutte le grammatical relation ma non so cosa farmene 
			        	Set<IndexedWord> descWords = sg.descendants(iWord);
			        	//Set<IndexedWord> compounds = sg.getChildrenWithReln(iWord, GrammaticalRelation.valueOf(Language.English,"compound"));
			        	addCompoundNamesToFrame("compound", frame, descWords, deps);
			        	//addCompoundNamesToFrame("nummod", frame, descWords, deps);
			        	for(IndexedWord desc : descWords)
			        	{
			        		//Nei discendenti compare anche il nodo stesso
			        		if(desc.originalText().equals(value))
			        			continue;
			        		//Bisogna effettuare il check per verificare che non sia una parola composta
			        		//Probabilmente un check sul grafo con il nome della relazione e l'indexedword
			        		
			        		//"Stopword removal"
			        		if(!filter(desc))
			        		{
			        			/*List<Entity> subEntities = matcher.getEntities(desc.word());
			        			if(subEntities.isEmpty())
			        				continue;*/
			        			frame.addInfo(desc.originalText(), null);
			        		}
			        	}
			        	System.out.println("Frame:"+frame);
			        }
			        
			     }
			}
		}
		return null;
	}
	private boolean filter(IndexedWord word)
	{
		String pos = word.get(CoreAnnotations.PartOfSpeechAnnotation.class);
		if(!(pos.equals("TO")||pos.equals("RB")||pos.equals("IN")||pos.equals("DT")||pos.equals(",")||pos.equals(".")))
		{
				return false;
		}
		return true;
	}
	private void addCompoundNamesToFrame(String relName, Frame frame, Set<IndexedWord> descWords, Collection<TypedDependency> deps)
	{
		for(TypedDependency dep : deps)
    	{
    		if(dep.reln().getShortName().equals(relName))
    		{
    			IndexedWord d = dep.dep();
    			IndexedWord g = dep.gov();
    			if(!filter(g)&&!filter(d))
    			{
    				frame.addInfo(d.originalText()+" "+g.originalText(), null);
    			}
    			descWords.remove(d);
    			descWords.remove(g);
    		}
    	}
	}
}

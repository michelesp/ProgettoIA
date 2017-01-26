package extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
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
	Map<String,Frame> map;
	public Extractor()
	{
		processor = new NLProcessor();
		matcher = new UMLSMatcher();
		map = new HashMap<>();
	}
	public Collection<Frame> buildFrame(String text, LocalDate date) throws IllegalAccessException, InvocationTargetException, IOException, Exception
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
				//System.out.println(sentence.toShorterString());
				//Grafo semantico con dipendenze e valori di pos 
				SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
				Collection<TypedDependency> deps = sg.typedDependencies();
				for(IndexedWord iWord: sg.vertexListSorted())
				{
					ArrayList<String> extractedInfo = new ArrayList<>();
					
					String value = iWord.originalText();
					String pos = iWord.get(CoreAnnotations.PartOfSpeechAnnotation.class);
					//Se non è un nome o un verbo non considero la creazione del frame
					if(!(pos.startsWith("NN")||pos.startsWith("VB")))
						continue;
					List<Entity> entities = matcher.getEntities(value);
					Set<String> set = null;
					Frame frame = null;
					if(entities.isEmpty())
						continue;
					else
					{
						frame = map.get(value);
						if(frame == null)
						{
							frame = new Frame(value,"",pos);
							map.put(value, frame);
						}
						else
							frame.addRecurrency();
						
						//Prendo quelle che io SUPPONGO siano le dipendenze grammaticali che dobbiamo considerare
						//Potrei accedervi prendendo dalla parola tutte le grammatical relation ma non so cosa farmene 
						Set<IndexedWord> descWords = sg.descendants(iWord);

						for(IndexedWord desc : descWords)
						{
							//Nei discendenti compare anche il nodo stesso
							if(desc.originalText().equals(value))
								continue;
							//"Stopword removal"
							if(!filter(desc))
							{
								extractedInfo.add(desc.originalText());
							}
						}
						mergeExtractedInfo(extractedInfo,deps);
						//System.out.println(extractedInfo);
						for(String s : extractedInfo)
							frame.addInfo(s, null);
						System.out.println(frame);
					}
				}
			}
		}
		return map.values();
	}
	private void mergeExtractedInfo(ArrayList<String> extractedInfo, Collection<TypedDependency> deps) {
		// TODO Auto-generated method stub
		//System.out.println("Before:\n"+extractedInfo);
		for(TypedDependency dep : deps)
		{
			if(dep.reln().getShortName().equals("nummod"))
			{
				IndexedWord d = dep.dep();
				IndexedWord g = dep.gov();
				if(!filter(g)&&!filter(d))
				{
				//	System.out.println("NAdding: "+g.originalText()+" "+d.originalText());
					extractedInfo.add(g.originalText()+" "+d.originalText());
				}
			//	System.out.println("NRemoving: "+g.originalText()+" and "+d.originalText());
				extractedInfo.remove(g.originalText());
				extractedInfo.remove(d.originalText());
			}
		}

		//System.out.println("After nmod:\n"+extractedInfo);
		for(TypedDependency dep : deps)
		{
			if(dep.reln().getShortName().equals("compound"))
			{
				IndexedWord d = dep.dep();
				IndexedWord g = dep.gov();
				//System.out.println("Compound "+d.originalText()+" "+g.originalText());
				if(!filter(g)&&!filter(d))
				{
					for (ListIterator<String> it = extractedInfo.listIterator(); it.hasNext(); ) {
						{  
							String s = it.next();
							if(s.startsWith(g.originalText()))
							{
							//	System.out.println("CRemoving: "+s);
								it.remove();
							//	System.out.println("CAdding: "+d.originalText()+" "+s);
								it.add(d.originalText()+" "+s);
							}
							if(s.startsWith(d.originalText()))
								it.remove();
						}
					}
					//extractedInfo.add(d.originalText()+" "+g.originalText());
				}
			}
		}

	//	System.out.println("After compound:\n"+extractedInfo);
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
}

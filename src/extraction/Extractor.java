package extraction;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ini4j.InvalidFileFormatException;

import com.google.common.collect.Sets;
import com.sun.java_cup.internal.runtime.Symbol;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.tokensregex.CoreMapExpressionExtractor;
import edu.stanford.nlp.ling.tokensregex.Env;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.ling.tokensregex.NodePattern;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import edu.stanford.nlp.util.CoreMap;
import gov.nih.nlm.nls.metamap.lite.EntityLookup4;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import protege.Frame;
import util.UMLStoProtegeCategotyMapping;

public class Extractor {

	NLProcessor processor;
	UMLSMatcher matcher;
	Map<String,Frame> map;
	String iWordP= "((?:[a-z][a-z]+))";
	String lParP= "((?:-LRB-))";
	String alphaNumP = "((?:[a-z][a-z]*[0-9]+[a-z0-9]*))";
	String oldFollowP = "((?:(,( )*[a-z][a-z]*[0-9]+[a-z0-9]*( )*((\\d*\\.\\d+)(?![-+0-9\\.]))?)*))";
	String followP =  "((?:(,( )*[a-z][a-z]*[0-9]+[a-z0-9]*( )*((\\d*\\.\\d+))?)*))";
	String spacesP = "( )*";
	String rParP = "((?:-RRB-))";
	String regexOriginal = iWordP+spacesP+lParP+spacesP+alphaNumP+spacesP+followP+spacesP+rParP;
	String regex2 = "((?:[a-z][a-z]*( )*[a-z0-9]*))";
	String regex = "((?:[a-z][a-z]+))( )*((?:-LRB-))( )*((?:[a-z][a-z]*( )*[a-z0-9]*))( )*((?:(,( )*[a-z][a-z]*( )*[a-z0-9]*( )*((\\d*\\.\\d+))?)*))( )*((?:-RRB-))";
	String regexFOriginal = "((?:[a-z][a-z]+))( )*((?:-LRB-))( )*((?:[a-z][a-z]*[0-9]+[a-z0-9]*))( )*((?:(,( )*[a-z][a-z]*[0-9]+[a-z0-9]*( )*((\\d*\\.\\d+))?)*))( )*((?:-RRB-))";
	Set<String> toFilter = Sets.newHashSet("CC","TO","RB","IN",":",".",",","RP","DT");
	UMLStoProtegeCategotyMapping mapping;

	public Extractor() throws InvalidFileFormatException, IOException
	{
		processor = new NLProcessor();
		matcher = new UMLSMatcher();
		map = new HashMap<>();
		mapping = new UMLStoProtegeCategotyMapping(new File("conf.ini"));
	}
	
	public void buildCBCFrame(String name, String value, LocalDateTime date) {
		Frame frame;
		name = normalize(name);
		value = normalize(value);
		if(map.get(name)==null){
			frame = new Frame(name, "noun", "CBC");
			map.put(name, frame);
		}
		else frame = map.get(name);
		frame.addInfo(value, date);
	}
	
	public void buildBloodGasAnalysisFrame(String name, String value, LocalDateTime date) {
		Frame frame;
		//System.err.println(name+" \t => \t "+normalize(name));
		//System.err.println(value+" \t => \t "+normalize(value));
		name = normalize(name);
		value = normalize(value);
		if(map.get(name)==null){
			frame = new Frame(name, "noun", "bloodgasanalysis");
			map.put(name, frame);
		}
		else frame = map.get(name);
		frame.addInfo(value, date);
	}
	
	public void buildFrame(String name, String value, LocalDateTime date) throws IllegalAccessException, InvocationTargetException, IOException, Exception {
		if(value.length()<=20)
		{
			Frame frame = map.get(normalize(value));
			if(frame == null)
			{
				String type = "noun";
				String category = "";
				EntityLookup4 el = new EntityLookup4();
				List<Entity> entityList = matcher.getEntities(value);
				if(entityList.size()>0)
				{
					String cui = entityList.get(0).getEvList().get(0).getConceptInfo().getCUI();
					Set<String> set = el.getSemanticTypeSet(cui);
					category = set.toString().substring(1, set.toString().length()-1);
				}
				frame = new Frame(normalize(value),type,mapping.mapping(category));
				map.put(normalize(value), frame);
			}
			else
				frame.addRecurrency();
			if(name!=null)
				frame.addInfo(normalize(name), date);
			
			System.out.println(frame);
		}
		else
		{
			buildFrame(value,date);
		}
	}

	public Collection<Frame> getFrames() {
		return map.values();
		
	}

	public Collection<Frame> buildFrame(String text, LocalDateTime date) throws IllegalAccessException, InvocationTargetException, IOException, Exception
	{

		//Usare una Mappa di Term,Frame cos� da scorrere il testo un'unica volta
		//e aggiungere informazioni al frame precedente eventualmente
		//ci siano informazioni da aggiungere
		EntityLookup4 el = new EntityLookup4();
		//Frasi annotate dall'NLPProcessor
		List<CoreMap> sentences = processor.getAnnotatedSentences(text);
		ArrayList<String> matchedPatterns = null;
		if (sentences != null && ! sentences.isEmpty()) {
			for(CoreMap sentence : sentences)
			{
				List<CoreLabel> tokens =  sentence.get(CoreAnnotations.TokensAnnotation.class);
				//System.out.println("Tokens: "+tokens.toString());
				Env env = TokenSequencePattern.getNewEnv();
				env.setDefaultStringMatchFlags(NodePattern.CASE_INSENSITIVE);
				env.setDefaultStringPatternFlags(Pattern.CASE_INSENSITIVE);
				TokenSequencePattern pattern = TokenSequencePattern.compile(env,"(?m) /"+regex+"/");
				TokenSequenceMatcher matcherT = pattern.getMatcher(tokens);
				matchedPatterns = new ArrayList<>();
				while (matcherT.find()) {
					matchedPatterns.add(matcherT.group().replaceAll("-LRB-", "(").replaceAll("-RRB-", ")"));
					//System.out.println("Matched:" +matcherT.group());
				}
				
				//System.out.println(sentence.toShorterString());
				//Grafo semantico con dipendenze e valori di pos 
				SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
				//Possibile ottimizzazione prendendo solo i figli con determinate relazioni?
				Collection<TypedDependency> deps = sg.typedDependencies();
				//System.out.println(deps.toString());
				for(IndexedWord iWord: sg.vertexListSorted())
				{
					ArrayList<String> extractedInfo = new ArrayList<>();
					String value = iWord.originalText();
					String pos = iWord.get(CoreAnnotations.PartOfSpeechAnnotation.class);
					//Se non � un nome o un verbo non considero la creazione del frame
					if(!(pos.startsWith("NN")||pos.startsWith("VB")))
						continue;
					List<Entity> entityList = matcher.getEntities(value);
					Frame frame = null;
					if(entityList.isEmpty())
						continue;
					else
					{
						//SemanticType con score massimo
						String cui = entityList.get(0).getEvList().get(0).getConceptInfo().getCUI();
						Set<String> set = el.getSemanticTypeSet(cui);
						frame = map.get(normalize(value));
						if(frame == null)
						{
							String category;
							if(pos.startsWith("NN"))
								category = "noun";
							else 
								category = "verb";
							frame = new Frame(normalize(value),category,mapping.mapping(set.toString().substring(1, set.toString().length()-1)));
						}
						else
							frame.addRecurrency();

						//Prendo quelle che io SUPPONGO siano le dipendenze grammaticali che dobbiamo considerare
						//Potrei accedervi prendendo dalla parola tutte le grammatical relation ma non so cosa farmene 
						Set<IndexedWord> descWords = sg.descendants(iWord);
						for(IndexedWord desc : descWords)
						{
							//Nei discendenti compare anche il nodo stesso
							if(normalize(desc.originalText()).equals(normalize(value)))
								continue;
							//"Stopword removal"
							if(!filter(desc))
							{
								extractedInfo.add(desc.originalText());
							}
						}
						mergeExtractedInfo(extractedInfo,deps);
						if(extractedInfo.size()>0)
						{
							mergeMatchedRegex(matchedPatterns,extractedInfo);
							for(String s : extractedInfo)
								frame.addInfo(normalize(s), date);
							map.put(normalize(value), frame);
							System.out.println(frame);
						}
					}
				}
			}
		}
		return map.values();
	}
	private void mergeExtractedInfo(ArrayList<String> extractedInfo, Collection<TypedDependency> deps) throws IllegalAccessException, InvocationTargetException, IOException, Exception {
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
					boolean added = false;
					for (ListIterator<String> it = extractedInfo.listIterator(); it.hasNext(); ) 
					{ 
						String s = it.next();
						if((s.contains(g.originalText()) || s.contains(d.originalText())) && !s.contains(g.originalText()+" "+d.originalText()) && !added)
						{
							
						
							System.out.println("NAdding: "+g.originalText()+" "+d.originalText());
							 
							it.add(g.originalText()+" "+d.originalText());
							buildFrame(d.originalText(),g.originalText(),null);
							added = true;
							//it.remove();
						}
					}
					//extractedInfo.add(g.originalText()+" "+d.originalText());
				}
				//System.out.println("NRemoving: "+g.originalText()+" and "+d.originalText());
				extractedInfo.remove(g.originalText());
				extractedInfo.remove(d.originalText());
			}
			if(dep.reln().getShortName().startsWith("aux"))
			{
				extractedInfo.remove(dep.dep().originalText());
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
					boolean found = false;
					for (ListIterator<String> it = extractedInfo.listIterator(); it.hasNext(); ) 
					{  
						String s = it.next();
						if(s.startsWith(g.originalText()))
						{
							found = true;
							//System.out.println("CRemoving: "+s);
							it.remove();
							//System.out.println("CAdding: "+d.originalText()+" "+s);
							it.add(d.originalText()+" "+s);
						}
					}
					for (ListIterator<String> it = extractedInfo.listIterator(); it.hasNext(); ) 
					{ 
						String s = it.next();
						if(s.equals(d.originalText()) && found)
							it.remove();
					}
				}
				//extractedInfo.add(d.originalText()+" "+g.originalText());
			}
		}

		//System.out.println("After compound:\n"+extractedInfo);
	}
	private void mergeMatchedRegex(ArrayList<String> matchedPatterns, ArrayList<String> extractedInfo)
	{
		for (String pattern : matchedPatterns ) 
		{ 
			for (ListIterator<String> it2 = extractedInfo.listIterator(); it2.hasNext(); ) 
			{ 
				String word = it2.next();

				if(pattern.indexOf(word)!=-1)
				{
					//System.out.println("adding, instead of:"+word);
					it2.remove();
				}
			}
			extractedInfo.add(pattern);
		}
	}
	//StopWord Removal
	private boolean filter(IndexedWord word)
	{
		String pos = word.get(CoreAnnotations.PartOfSpeechAnnotation.class);
		if(toFilter.contains(pos))
		{
			return true;
		}
		return false;

		/*if(!(pos.equals("TO")||pos.equals("RB")||pos.equals("IN")||pos.equals("DT")||pos.equals(",")||pos.equals(".")||pos.equals(":")))
		{
			return false;
		}
		return true;*/
	}

	private String normalize(String str) {
		try {
			return URLEncoder.encode(str.toLowerCase().trim(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
}

package extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.ini4j.InvalidFileFormatException;

import com.google.common.collect.Sets;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.tokensregex.Env;
import edu.stanford.nlp.ling.tokensregex.NodePattern;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TypedDependency;
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
	Set<String> infectious = Sets.newHashSet("fngs","bact","virs");
	UMLStoProtegeCategotyMapping mapping;

	public Extractor() throws InvalidFileFormatException, IOException
	{
		processor = new NLProcessor();
		matcher = new UMLSMatcher();
		map = new HashMap<>();
		mapping = new UMLStoProtegeCategotyMapping(new File("conf.ini"));
	}

	public void buildCBCFrame(String name, String value, LocalDateTime date) throws IllegalAccessException, InvocationTargetException, Exception {
		Frame frame;
		name = normalize(name);
		value = normalize(value);
		EntityLookup4 el = new EntityLookup4();
		List<Entity> entityList = matcher.getEntities(name);
		if(entityList.size()>0)
		{
			String cui = entityList.get(0).getEvList().get(0).getConceptInfo().getCUI();
			Set<String> set = el.getSemanticTypeSet(cui);
			String category = set.toString().substring(1, set.toString().length()-1);
			if(infectious.contains(category))
			{
				System.out.println("SuspectedInfection");
				suspectInfection(name,category,value,date);
			}
		}
		if(map.get(name)==null){
			frame = new Frame(name, "noun", "CBC");
			map.put(name, frame);
		}
		else frame = map.get(name);
		frame.addInfo(value, date);
	}

	public void buildBloodGasAnalysisFrame(String name, String value, LocalDateTime date) throws IllegalAccessException, InvocationTargetException, Exception {
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
		if(name==null)
			return;
		if(value.length()<=20)
		{
			Frame frame = map.get(normalize(name));
			if(frame == null)
			{
				String type = "noun";
				String category = "";
				EntityLookup4 el = new EntityLookup4();
				List<Entity> entityList = matcher.getEntities(name);
				if(entityList.size()>0)
				{
					String cui = entityList.get(0).getEvList().get(0).getConceptInfo().getCUI();
					Set<String> set = el.getSemanticTypeSet(cui);
					category = set.toString().substring(1, set.toString().length()-1);
					if(infectious.contains(category))
					{
						System.out.println("SuspectedInfection");
						suspectInfection(name,category,value,date);
					}
				}

				frame = new Frame(normalize(name),type,mapping.mapping(category));
				map.put(normalize(name), frame);
			}
			else
				frame.addRecurrency();
			if(value!=null)
				frame.addInfo(normalize(value), date);

			//System.out.println(frame);
		}
		else
		{
			buildFrame(value,date);
		}
	}

	public Collection<Frame> getFrames() {
		return map.values();

	}

	public void clear() {
		map.clear();
	}

	public Collection<Frame> buildFrame(String text, LocalDateTime date) throws IllegalAccessException, InvocationTargetException, IOException, Exception
	{
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
							String semanticType = set.toString().substring(1, set.toString().length()-1);
							if(infectious.contains(semanticType))
								suspectInfection(null, null, value, date);
							frame = new Frame(normalize(value),category,mapping.mapping(semanticType));
						}
						else
							frame.addRecurrency();

						//Prendo quelle che sono le dipendenze grammaticali che dobbiamo considerare
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
						mergeExtractedInfo(extractedInfo,deps,date);
						if(extractedInfo.size()>0)
						{
							mergeMatchedRegex(matchedPatterns,extractedInfo);
							for(String s : extractedInfo)
							{
								entityList = matcher.getEntities(s);
								if(entityList.size()==0 || entityList.get(0).getEvList().size()==0)
									continue;
								cui = entityList.get(0).getEvList().get(0).getConceptInfo().getCUI();
								set = el.getSemanticTypeSet(cui);
								String semanticType = set.toString().substring(1, set.toString().length()-1);
								if(infectious.contains(semanticType))
									suspectInfection(null, null, s, date);
								frame.addInfo(normalize(s), date);
							}
							map.put(normalize(value), frame);
							//System.out.println(frame);
						}
					}
				}
			}
		}
		return map.values();
	}
	private void mergeExtractedInfo(ArrayList<String> extractedInfo, Collection<TypedDependency> deps, LocalDateTime date) throws IllegalAccessException, InvocationTargetException, IOException, Exception {
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
							it.add(g.originalText()+" "+d.originalText());
							//	System.out.println("Call to buildframe: "+g.originalText()+" "+d.originalText());
							if(org.apache.commons.lang3.StringUtils.isNumeric(d.originalText()))							
								buildFrame(g.originalText(),d.originalText(),date);
							else	
								buildFrame(d.originalText(),g.originalText(),date);
							added = true;
							//it.remove();
						}
					}
					extractedInfo.add(g.originalText()+" "+d.originalText());
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
	private void suspectInfection(String name,String nameCategory, String value, LocalDateTime date) throws IllegalAccessException, InvocationTargetException, Exception
	{
		String category = null;
		EntityLookup4 el = new EntityLookup4();
		List<Entity> entityList = matcher.getEntities(value);
		if(entityList.size()>0)
		{
			String cui = entityList.get(0).getEvList().get(0).getConceptInfo().getCUI();
			Set<String> set = el.getSemanticTypeSet(cui);
			category = set.toString().substring(1, set.toString().length()-1);
		}
		if(infectious.contains(nameCategory))
		{
			Frame f = map.get("infection");
			if(f==null)
			{
				f = new Frame("infection","noun","symptom");
				map.put("infection",f);
			}
			/*if(name!=null)
			{
				f.addInfo(name, date);
				f.addInfo(nameCategory, date);
			}*/
			f.addInfo(value, date);
			//f.addInfo(category, date);
		}
	}
}

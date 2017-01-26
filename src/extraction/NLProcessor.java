package extraction;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class NLProcessor {

	StanfordCoreNLP pipeline;
	public NLProcessor()
	{
		 Properties props = new Properties();
		 props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse");
		 pipeline = new StanfordCoreNLP(props);
	}
	
	public List<CoreMap> getAnnotatedSentences(String text)
	{
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		return annotation.get(CoreAnnotations.SentencesAnnotation.class);
	}
}

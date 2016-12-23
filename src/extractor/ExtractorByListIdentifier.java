package extractor;

import java.util.List;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;

public abstract class ExtractorByListIdentifier {
	protected StanfordCoreNLP pipeline;

    public ExtractorByListIdentifier(){
    	this.pipeline= new StanfordCoreNLP(
				PropertiesUtils.asProperties(
						"annotators", "tokenize, ssplit, pos, lemma, ner, parse"));
    }
	public abstract List<String> extractFacts(String phrase);

}

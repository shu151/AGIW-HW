import java.io.*;
import java.util.*;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import edu.stanford.nlp.coref.CorefCoreAnnotations;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.io.EncodingPrintWriter.out;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.DependentsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

public class Lettore {
	public static void main(String[] args) throws IOException, InterruptedException {

	    TsvParserSettings settings = new TsvParserSettings();
	    //the file used in the example uses '\n' as the line separator sequence.
	    //the line separator sequence is defined here to ensure systems such as MacOS and Windows
	    //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
	    settings.getFormat().setLineSeparator("\n");

	    // creates a TSV parser
	    TsvParser parser = new TsvParser(settings);

	    // parses all rows in one go.
	    List<String[]> allRows = parser.parseAll(getFileReader("test_sentences.tsv"));
	    //System.out.println(allRows.get(0)[3].replaceAll("\\[\\[","").replaceAll("\\|[[a-z]*[A-Z]*[0-9]*[.]*]*\\]\\]",""));
	    for(String[] elem : allRows) {
	    	// per il momento lascio gli underscore nelle entita' con piu' parole
	    	String el = elem[3].replaceAll("\\[\\[","").replaceAll("\\|[[a-z]*[A-Z]*[0-9]*[.]*[_]*]*\\]\\]","");
	    	//String el = elem[3].replaceAll("\\[\\[","").replaceAll("\\|[[a-z]*[A-Z]*[0-9]*[.]*[_]*]*\\]\\]","").replaceAll("_", " ");
	    	System.out.println(el);
	    	analizzatore(el,elem[3]);
	    	Thread.sleep(20000);
	    }
	
	}
	public static Reader getFileReader(String absolutePath) throws UnsupportedEncodingException, FileNotFoundException {
	    return new InputStreamReader(new FileInputStream(new File(absolutePath)), "UTF-8");
	}
	public static void analizzatore(String text, String originalText) throws FileNotFoundException{

		// build pipeline
		StanfordCoreNLP pipeline = new StanfordCoreNLP(
				PropertiesUtils.asProperties(
						"annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment"));

		// read some text in the text variable
		//String text = "Internet Start served as the default home page for Internet Explorer and offered basic information such as news , weather , sports , stocks , entertainment reports , links to other websites on the Internet , articles by Microsoft staff members , and software updates for Windows 8.";
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		File file = new File("C:/Users/Daniele/workspace/NaturalLanguage/dati/file.txt");
		file.getParentFile().mkdirs();
		PrintWriter printWriter = new PrintWriter(file);
		
		CoreMap sentence1 = sentences.get(0);
		Tree tree = sentence1.get(TreeCoreAnnotations.TreeAnnotation.class);
		tree.pennPrint(printWriter);
		//out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
		
		
		// ogni sentence è una frase, un punto divide le varie sentence che vanno iterate nel foreach
		CoreMap sentence = sentences.get(0);
//		for(CoreMap sentence: sentences) {
		String fraseSemantica = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST);
		List<String> listaFraseSemantica = Arrays.asList(fraseSemantica.split("\\n"));
		List<String> frasi = new ArrayList<>();
		int numeriSuchAs = 0;
		int parole = 0;
		for(String dipendenza : listaFraseSemantica) {
			System.out.println(dipendenza);
			if (dipendenza.indexOf("nmod:such_as")>-1) {
				int indice = dipendenza.indexOf('-', dipendenza.indexOf("-")+1);
				if (numeriSuchAs==0) {
					parole = Integer.parseInt(dipendenza.substring(indice+1, dipendenza.indexOf(")")))-1;
					numeriSuchAs++;
				}
				String suchasWord = dipendenza.substring(dipendenza.indexOf(", ")+2, dipendenza.indexOf("-", dipendenza.indexOf("-")+1));
				numeriSuchAs++;
				
				int i = 0;
				String frase = "";
				for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
					if (parole>i) {
						frase = frase + token.get(TextAnnotation.class) + " ";
						i++;
					}
				}
				frase = frase + suchasWord;
				frasi.add(frase);
			}
		}
		System.out.println(numeriSuchAs);
		for(String frase : frasi) {
			System.out.println("Frase:" + frase);
			relazioniFrasi(frase);
		}
		
		//IndexedWord dep2 = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).getFirstRoot();

		// lascio per esempio ma non serve
		// traversing the words in the current sentence
		// a CoreLabel is a CoreMap with additional token-specific methods
		for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
			// this is the text of the token
			String word = token.get(TextAnnotation.class);
			System.out.println(word);
			// this is the POS tag of the token
			String pos = token.get(PartOfSpeechAnnotation.class);
			System.out.println(pos);
			// this is the NER label of the token
			String ne = token.get(NamedEntityTagAnnotation.class);
			//out.println(ne);
		}
//		}
	}
	public static void relazioniFrasi(String frase) {
		
	}
}
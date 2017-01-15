package phrasesExpansion;

import java.io.*;

import extractor.Extractor;
import fileManagement.FileInteractor;
import parser.ListSentencesFilter;
import parser.TSVSentencesExtractor;
import prefilter.ParallelFilter;
import relationIdentifier.RelationalIdentifier;
import splitter.PhraseSplitter;

import java.util.LinkedList;
import java.util.List;

public class mainPhrasesAnalysis {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//creo due file Accepted.tsv e Refused.tsv
		String pathToInputFile="evaluationcorpus.tsv";
		boolean useMetrics=false; //le frasi del file di input devono essere etichettate!!! (tab Y otab N)
		ParallelFilter pf = new ParallelFilter();
		pf.filterSentences(pathToInputFile,useMetrics);
		
		accepted();
//		refused();
	}
	private static void accepted() throws UnsupportedEncodingException, FileNotFoundException {
		List<List<String>>result=new LinkedList<List<String>>();
		ListSentencesFilter ls = new ListSentencesFilter();
		TSVSentencesExtractor t = new TSVSentencesExtractor();
		List<String[]> allRows = t.getAllSentencesFromTSV("Accepted.tsv");
		Extractor eb = new Extractor();
		FileInteractor f = new FileInteractor();
		RelationalIdentifier ri = new RelationalIdentifier();
		for(String[] phrase : allRows){
			try {
				List<String> filtPhraseWithEntities = ls.removeNoWordContent(phrase[3]);
				List<String> ar = eb.extractFacts(filtPhraseWithEntities);
				result.add(ar);
				for(String relation : ar){
					f.writeFile(relation,"acceptedPhrase2");
					f.writeFile(ri.isRelational(relation)+"\n","acceptedPhrase2");
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	// da vedere
	private static void refused() throws UnsupportedEncodingException, FileNotFoundException {
		List<List<String>>result=new LinkedList<List<String>>();
		ListSentencesFilter ls = new ListSentencesFilter();
		TSVSentencesExtractor t = new TSVSentencesExtractor();
		List<String[]> allRows = t.getAllSentencesFromTSV("Refused.tsv");
		Extractor eb = new Extractor();
		FileInteractor f = new FileInteractor();
		RelationalIdentifier ri = new RelationalIdentifier();
		
		PhraseSplitter splitter = new PhraseSplitter();
		for(String[] phrase : allRows){
			List<String> phraseSplitted = splitter.splitPhrase(phrase[3]);
//			f.writeFile(relation,"refusedPhrase");
		}
	}
}


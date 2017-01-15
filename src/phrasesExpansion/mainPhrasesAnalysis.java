package phrasesExpansion;

import java.io.*;

import extractor.Extractor;
import extractor.FactsListExtractor;
import fileManagement.FileInteractor;
import parser.TSVSentencesUtility;
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
		
		FactsListExtractor fe= new FactsListExtractor();
		fe.do_extractFacts();
//		refused();
	}

	// da vedere
	private static void refused() throws UnsupportedEncodingException, FileNotFoundException {
		List<List<String>>result=new LinkedList<List<String>>();
		TSVSentencesUtility tSVSentencesUtility = new TSVSentencesUtility();
		List<String[]> allRows = tSVSentencesUtility.getAllSentencesFromTSV("Refused.tsv");
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


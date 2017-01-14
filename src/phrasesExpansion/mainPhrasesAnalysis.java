package phrasesExpansion;

import java.io.*;

import extractor.Extractor;
import fileManagement.FileInteractor;
import parser.ListSentencesFilter;
import parser.TSVSentencesExtractor;
import prefilter.ParallelFilter;

import java.util.LinkedList;
import java.util.List;

public class mainPhrasesAnalysis {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//creo due file Accepted.tsv e Refused.tsv
		String pathToInputFile="evaluationcorpus.tsv";
		boolean useMetrics=false; //le frasi del file di input devono essere etichettate!!! (tab Y otab N)
		ParallelFilter pf = new ParallelFilter();
		pf.filterSentences(pathToInputFile,useMetrics);

		List<List<String>>result=new LinkedList<List<String>>();
		ListSentencesFilter ls = new ListSentencesFilter();
		TSVSentencesExtractor t = new TSVSentencesExtractor();
		List<String[]> allRows = t.getAllSentencesFromTSV("Accepted.tsv");
//		List<String[]> allRows = t.getAllSentencesFromTSV("test_sentences.tsv");
		Extractor eb = new Extractor();
		FileInteractor f = new FileInteractor();
		for(String[] phrase : allRows){
			System.out.println(phrase[3]);
			List<String> filtPhraseWithEntities = ls.removeNoWordContent(phrase[3]);
			String filtphrase = filtPhraseWithEntities.get(0);
			// esamino la frase
			List<String> ar = eb.extractFacts(filtphrase);
			result.add(eb.extractFacts(filtphrase));
			for(String relation : ar){
				f.writeFile(relation+"\n");
			}
		}
		
		//per scrivere su file
//		FileInteractor f = new FileInteractor();
//		for(List<String> s:result){
//			for(String relation : s){
//				f.writeFile(relation+"\n");
//			}
//		}
	}
		


//		for(String kindOfListId:map.keySet()){
//			List<String[]>phrases=map.get(kindOfListId);
//			// creo nuova istanza delle classi ExtractorBy in base al tipo
//			ExtractorByListIdentifier eb=factory.buildExtractor(kindOfListId);
//			f.writeFile(kindOfListId+": "+phrases.size()+"\n");
//			if(kindOfListId!="NoList")
//				for(String[] phrase:phrases){
//					List<String> filtPhraseWithEntities = ls.removeNoWordContent(phrase[3]);
//					String filtphrase = filtPhraseWithEntities.get(0);
//					// esamino la frase
//					result.add(eb.extractFacts(filtphrase));
//				}
//		}
//		int count = 1;
//		for(List<String> s:result){
//			if(s!=null){
//				System.out.println("Frase iniziale:");
//				System.out.println(s.get(0));
//				String co = String.valueOf(count);
//				f.writeFile(co);
//				count = count+1;
//				f.writeFile(s.get(0) + "\n");
//				System.out.println("Fatti estratti:");
//				int dim=s.size();
//				for(int counter=1;counter<dim;counter++){
//					System.out.println(s.get(counter));
//					f.writeFile(s.get(counter) + "\n");
//				}
//			} else{
//				
//			}
//		}
	}

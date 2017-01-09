package phrasesExpansion;

import java.io.*;
import java.util.*;

import extractor.ExtractionByListIdentifierFactory;
import extractor.ExtractorByListIdentifier;
import fileManagement.FileInteractor;
import parser.ListSentencesFilter;
import parser.TSVSentencesExtractor;
import java.util.LinkedList;
import java.util.List;

public class testDef {
	public static void main(String[] args) throws IOException, InterruptedException {

		List<List<String>>result=new LinkedList<List<String>>();

		TSVSentencesExtractor t= new TSVSentencesExtractor();
		List<String[]> allRows = t.getAllSentencesFromTSV("test_sentences.tsv");

		ListSentencesFilter ls= new ListSentencesFilter();
		Map<String,List<String[]>>map=ls.getOnlyListSentences(allRows);

		ExtractionByListIdentifierFactory factory= new ExtractionByListIdentifierFactory();
		
		//per scrivere su file
		FileInteractor f = new FileInteractor();

		for(String kindOfListId:map.keySet()){
			List<String[]>phrases=map.get(kindOfListId);
			// creo nuova istanza delle classi ExtractorBy in base al tipo
			ExtractorByListIdentifier eb=factory.buildExtractor(kindOfListId);
			f.writeFile(kindOfListId+": "+phrases.size()+"\n");
			if(kindOfListId!="NoList")
				for(String[] phrase:phrases){
					List<String> filtPhraseWithEntities = ls.removeNoWordContent(phrase[3]);
					String filtphrase = filtPhraseWithEntities.get(0);
					// esamino la frase
					result.add(eb.extractFacts(filtphrase));
				}
		}
		int count = 1;
		for(List<String> s:result){
			if(s!=null){
				System.out.println("Frase iniziale:");
				System.out.println(s.get(0));
				String co = String.valueOf(count);
				f.writeFile(co);
				count = count+1;
				f.writeFile(s.get(0) + "\n");
				System.out.println("Fatti estratti:");
				int dim=s.size();
				for(int counter=1;counter<dim;counter++){
					System.out.println(s.get(counter));
					f.writeFile(s.get(counter) + "\n");
				}
			} else{
				
			}
		}
	}
}
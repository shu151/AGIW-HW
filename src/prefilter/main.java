package prefilter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import parser.TSVSentencesExtractor;

public class main {

	public static void main(String[] args) {
		System.out.println("start");

		ScoreGetter sg= new ScoreGetter();

		List<List<String>>result=new LinkedList<List<String>>();

		TSVSentencesExtractor t= new TSVSentencesExtractor();
		try {
			PrintWriter writer = new PrintWriter("FilteredPhrasesAccepted.tsv", "UTF-8");
			PrintWriter writer1 = new PrintWriter("FilteredPhrasesRefused.tsv", "UTF-8");


			List<String[]> allRows = t.getAllSentencesFromTSV("test_sentences.tsv");
			for(String[] TSVsentence : allRows) {
				
				if(TSVsentence[3]!=""&&TSVsentence[3]!=null){
					if(sg.getScore(TSVsentence[3])>=1){
						System.out.println("ok");
						System.out.println(TSVsentence[3]);}
					else{
						System.out.println("ko");
						System.out.println(TSVsentence[3]);}
				}
			}

		} catch (UnsupportedEncodingException e) {
			System.out.println("errore");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("errore");
			e.printStackTrace();
		}
		System.out.println("end");




	}

}

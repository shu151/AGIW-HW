package prefilter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import parser.TSVSentencesExtractor;
import relationIdentifier.RelationalIdentifier;

public class main {

	public static void main(String[] args) {

		System.out.println("start");

		ScoreGetter sg= new ScoreGetter();

		List<List<String>>result=new LinkedList<List<String>>();

		TSVSentencesExtractor t= new TSVSentencesExtractor();

		try {
			PrintWriter writer = new PrintWriter("Accepted100.tsv", "UTF-8");
			PrintWriter writer1 = new PrintWriter("Refused100.tsv", "UTF-8");

			//	PrintWriter writer1 = new PrintWriter("FilteredPhrasesRefused.tsv", "UTF-8");


			List<String[]> allRows = t.getAllSentencesFromTSV("test_sentences.tsv");
			int i=0;
			
			double threashold=sg.getThreashold();
			for(String[] TSVsentence : allRows) {
				
				try{
					if(sg.getScore(TSVsentence[3])>=threashold)
						writer.println(TSVsentence[3]);
					else
						writer1.println(TSVsentence[3]);
				}catch(NullPointerException e){
					System.out.println(" riga vuota");}
				
			} }catch (UnsupportedEncodingException e) {
				System.out.println("errore");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.out.println("errore");
				e.printStackTrace();

			}
			System.out.println("end");

	/*	
		//TODO si blocca!!!! sulla regex delle 3 list
		String entityRegex=".*\\[\\[[a-zA-Z0-9_()/,\\.&-]{0,70}\\|m.[a-zA-Z0-9_]*\\]\\].*";
		String notEntityRegex="[a-zA-Z0-9_]{0,20}";
		String containsColonFollowedByListRegex=".*: ((("+entityRegex+"|"+notEntityRegex+") ){1,5}, )*(("+entityRegex+"|"+notEntityRegex+") ){1,5}and( ("+entityRegex+"|"+notEntityRegex+")){1,5}.*";

	//	String phrase="This concept is invoked to explain how different topographic heights can exist at [[Earth|m.02j71]] 's surface .";
		String phrase="Some were especially noted for their hospitality , such as [[Canowie_Station|m.0wyqk0_]] in South [[Australia|m.0chghy]] which around 1903 provided over 2,000 [[Swagman|m.0198g6]] each year with their customary two meals and a bed .";

		ScoreGetter sg= new ScoreGetter();
		System.out.println(sg.getScore(phrase));
		System.out.println(phrase);
	//	System.out.println(phrase.matches(containsColonFollowedByListRegex)); */
			 

		}

	}

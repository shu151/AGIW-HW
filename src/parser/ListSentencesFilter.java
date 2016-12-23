package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ListSentencesFilter {
	/**
	 * Restituisce una mappa del tipo suchAs->lista delle frasi di tipo such as
	 * un elemento della lista è un array con questi campi:
	 * 0: id(nome) entità principale della pagina wikipedia
	 * 1: id(codice) entità principale in freebase
	 * 2: numero di entità rilevate nella frase
	 * 3: frase
	 * @param sentences: "frasi" (righe) prese dal TSV secondo lo schema descritto precedentemente
	 * @return una mappa del tipo <key: tipo della lista(es suchas),value:lista di array delle frasi
	 * secondo sempre lo stesso schema>
	 */
	public Map<String,List<String[]>> getOnlyListSentences(List<String[]> sentences){
		Map<String,List<String[]>> updatedList=new HashMap<String,List<String[]>>();
		for(String[] TSVsentence : sentences) {
			//il try catch serve perche alcune righe nel tsv sono vuote
			try {
				String kindOfList=getKindOfList(TSVsentence[3]);
				if(updatedList.containsKey(kindOfList))
					updatedList.get(kindOfList).add(TSVsentence);
				else{
					LinkedList<String[]> phrase=new LinkedList<String[]>();
					phrase.add(TSVsentence);
					updatedList.put(kindOfList, phrase);}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return updatedList;
	}
	/**
	 * 
	 * @param phrase
	 * @return
	 */
	private String getKindOfList(String phrase){

		String regexSuchAs = ".*\\|m\\..*such as.*\\|m\\..*";
		String regexIncluding = ".*\\|m\\..*including.*\\|m\\..*";

		if(Pattern.matches(regexSuchAs, phrase))
			return "SuchAs";

		else if(Pattern.matches(regexIncluding, phrase))
			return "Including";

		else
			return "NoList";
	}
	/**
	 * removeNoWordContent ha in input una frase presa dal file TSV e ne restituisce una lista
	 * @param phrase: frase presa dal TSV
	 * @return result: lista di stringhe, in prima posizione abbiamo la frase pulita dalle parentesi tra
	 * le entità, nelle posizioni successive abbiamo le stringhe delle varie entità presenti es entità:
	 * [[Microsoft|m.04sv4]]
	 */
	public List<String> removeNoWordContent(String phrase){
		List<String> result = new ArrayList<>();
		result.add(phrase.replaceAll("\\[\\[","").replaceAll("\\|[[a-z]*[A-Z]*[0-9]*[.]*[_]*]*\\]\\]","").replaceAll("_", " "));
		int beginIndex = 0;
		int endIndex;
		while (phrase.indexOf("[[",beginIndex)!=-1){
			beginIndex = phrase.indexOf("[[",beginIndex);
			endIndex = phrase.indexOf("]]",beginIndex+1)+2;
			String entity = phrase.substring(beginIndex, endIndex);
			beginIndex = endIndex;
			result.add(entity);
		}
		return result;
	}
}
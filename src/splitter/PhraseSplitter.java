package splitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xml.internal.ws.util.StringUtils;

public class PhraseSplitter {

	public Pattern entity_regex = Pattern.compile("\\[\\[[A-Za-z0-9\\p{Punct}]+\\|m\\.[a-z0-9\\p{Punct}]+\\]\\]");
	//This method will return a list which contains: the phrase, then the number n of entities, then the
	//n entities and following the n snippets of phrase between the entities
	public List<String> splitPhrase(String phrase){
		List<String> result = new ArrayList<String>();
		List<String> entities = new ArrayList<String>();
		List<String> phrase_snippets = new ArrayList<String>();
		List<String> acc = new ArrayList<String>();
		result.add(phrase);

		Matcher m = entity_regex.matcher(phrase);
		while (m.find()) {
			acc.add(m.start() + " " + m.end());
			entities.add(m.group());
		}
		entities.toArray(new String[0]);
		result.add(String.valueOf(entities.size()));	//this size will be the same for phrase_snippets
		result.addAll(entities);

		for (int i = 0; i < acc.size()-1; i++) {
			String starting_index = acc.get(i).split(" ")[1];
			String ending_index = acc.get(i+1).split(" ")[0];
			result.add(phrase.substring(Integer.parseInt(starting_index)+1, Integer.parseInt(ending_index)-1));
		}
		String last_index = acc.get(acc.size()-1).split(" ")[1];
		result.add(phrase.substring(Integer.parseInt(last_index)+1));

		return result;
	}
}

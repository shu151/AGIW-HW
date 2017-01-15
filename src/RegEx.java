import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.*;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import edu.stanford.nlp.patterns.Pattern;
import fileManagement.FileInteractor;

public class RegEx {
	public static void main(String[] args) throws IOException, InterruptedException {

	    TsvParserSettings settings = new TsvParserSettings();
	    settings.getFormat().setLineSeparator("\n");
	    TsvParser parser = new TsvParser(settings);
	    int count = 0;
	    FileInteractor f = new FileInteractor();
	    f.deleteFile("outfilename");
	    List<String[]> allRows = parser.parseAll(getFileReader("test_sentences.tsv"));
	    for(String[] elem : allRows) {
	    	String el = "";
	    	if(elem[3] != null)
	    		el = elem[3];
	    	if(!java.util.regex.Pattern.matches(".*such as.*", el))
	    		f.writeFile(el + "\n","outfilename");
	    		//System.out.println(el + "\n");//count++;
	    	
	    	//System.out.println("ciao");
	    }
	    //System.out.println(count);
	
	}
	
	public static Reader getFileReader(String absolutePath) throws UnsupportedEncodingException, FileNotFoundException {
	    return new InputStreamReader(new FileInputStream(new File(absolutePath)), "UTF-8");
	}
}

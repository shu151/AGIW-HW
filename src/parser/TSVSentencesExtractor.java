package parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;


public class TSVSentencesExtractor {
	private TsvParserSettings settings;
	private TsvParser parser;
	
	public TSVSentencesExtractor(){
		this.settings = new TsvParserSettings();
	    settings.getFormat().setLineSeparator("\n");
	     this.parser= new TsvParser(settings);

	}

	private  Reader getFileReader(String absolutePath) throws UnsupportedEncodingException, FileNotFoundException {
	    return new InputStreamReader(new FileInputStream(new File(absolutePath)), "UTF-8");
	}
	
	public List<String[]> getAllSentencesFromTSV(String pathToFile) throws UnsupportedEncodingException, FileNotFoundException{
		return  parser.parseAll(this.getFileReader(pathToFile));
	}
	

	
}

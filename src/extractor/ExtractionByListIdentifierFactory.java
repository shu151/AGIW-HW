package extractor;


public class ExtractionByListIdentifierFactory  {

	public ExtractorByListIdentifier buildExtractor(String listIdentifier) { 
		ExtractorByListIdentifier extractor= null;

		try {
			String className = "extractor.ExtractorBy"; 
			className += listIdentifier;
			extractor = (ExtractorByListIdentifier)Class.forName(className).newInstance();
		} catch (Exception e) {
			extractor = new ExtractionNotPossible();
		}
		return extractor;
	}
}
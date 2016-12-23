package extractor;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import parser.XMLPars;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;


public class ExtractorByIncluding extends ExtractorByListIdentifier{

	@Override
	public List<String> extractFacts(String phrase) {

		String dependenciesXML=null;

    	Annotation document = new Annotation(phrase);
    	pipeline.annotate(document);
    	
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for(CoreMap sentence: sentences) {
			dependenciesXML= sentence.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.XML);
			}

		String cop=null;
		Boolean isFirstOccurrence=true;
			    
	    try {
	    	XMLPars x=new XMLPars(dependenciesXML);
	        NodeList nodes = x.getNodesByTag("dep");

	        List<String>listExtracted=new LinkedList<String>();
	        String phrase1=null;
	        String subject=null;

	        for (int i = 0; i < nodes.getLength(); i++) {
	        
	           Node current_Node=nodes.item(i);
	           String dep_type=current_Node.getAttributes().getNamedItem("type").getNodeValue();
	        	
	           if(dep_type.equals("nsubj")&&isFirstOccurrence){
	        	   isFirstOccurrence=false;
	           Element element = (Element) nodes.item(i);

	           NodeList name = element.getElementsByTagName("governor");
	           Element line = (Element) name.item(0);
	      //     System.out.println("governor: " + getCharacterDataFromElement(line));
	           phrase1=getCharacterDataFromElement(line);

	           NodeList title = element.getElementsByTagName("dependent");
	           line = (Element) title.item(0);
	      //     System.out.println("dependent: " + getCharacterDataFromElement(line));
	           subject=getCharacterDataFromElement(line);}
	           
	           if(dep_type.equals("nmod:including")){
	               Element element = (Element) nodes.item(i);

	               NodeList name = element.getElementsByTagName("governor");
	               Element line = (Element) name.item(0);
	     //          System.out.println("governor: " + getCharacterDataFromElement(line));

	               NodeList title = element.getElementsByTagName("dependent");
	               line = (Element) title.item(0);
	     //          System.out.println("dependent: " + getCharacterDataFromElement(line));
	               listExtracted.add(getCharacterDataFromElement(line));}
	            
	        

	           if(dep_type.equals("cop")){
	               Element element = (Element) nodes.item(i);

	               NodeList name = element.getElementsByTagName("governor");
	               Element line = (Element) name.item(0);
	     //          System.out.println("governor: " + getCharacterDataFromElement(line));

	               NodeList title = element.getElementsByTagName("dependent");
	               line = (Element) title.item(0);
	     //          System.out.println("dependent: " + getCharacterDataFromElement(line));
	               cop=getCharacterDataFromElement(line);}
	            }

	           
	        List <String> result=new LinkedList<String>();
	        result.add(phrase);
	        for(String s:listExtracted){
	        	String v=(subject+" ");
	        	if(cop!=null)
	        	v=v+cop+" ";
	        	v=v+phrase1+" ";
	        	v=v+s;
	        	result.add(v);}
	        	return result;

	        
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    
	}
	

	
public  String getCharacterDataFromElement(Element e) {
		    Node child = e.getFirstChild();
		    if (child instanceof CharacterData) {
		       CharacterData cd = (CharacterData) child;
		       return cd.getData();
		    }
		    return "?";
		  }
}
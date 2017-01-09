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


public class ExtractorBySuchAs extends ExtractorByListIdentifier{

	@Override
	// ritorna lista contentente n valori: frase, n-1 fatti
	public List<String> extractFacts(String phrase) {

		String dependenciesXML=null;

		// libreria stanford CoreNLP
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
			
			// scorro la lista dei nodi, un nodo è una dipendenza
			for (int i = 0; i < nodes.getLength(); i++) {
				//System.out.println(nodes.getLength());

				Node current_Node=nodes.item(i);
				/* es frammento XML
				 * <dep type="nsubj">
				 * 		<governor idx="8">supports</governor>
				 * 		<dependent idx="1">Eclipse</dependent>
				 * </dep>
				 */
				// stringa che indica il tipo di dipendenza (es nmod, nsubj etc)
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

				if(dep_type.equals("nmod:such_as")){
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
				if(phrase1!=null)
					v=v+phrase1+" ";
				if(s!=null)
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
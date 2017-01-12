package extractor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	/**
	 * governor2dependents restituisce una mappa per una specifica dipendenza dove la chiave è 
	 * un governor mentre i valori aono una lista di dependent di quel governor
	 * @param nodes: lista delle dipendenze in formato XML
	 * @param depType: dipendenza che si vuole cercare
	 * @return governor2dependents: mappa per una specifica dipendenza dove la chiave è 
	 * un governor mentre i valori aono una lista di dependent di quel governor
	 */
	private Map<Element, List<Element>> governor2dependents(NodeList nodes, String depType){
		Map<Element, List<Element>> governor2dependents = new HashMap<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currentNode = nodes.item(i);
			Element currentElement = (Element) nodes.item(i);
			String dep_type = currentNode.getAttributes().getNamedItem("type").getNodeValue();
			if(dep_type.contains(depType)){
				Element governor = (Element) currentElement.getElementsByTagName("governor").item(0);
				Element support = null;
				boolean isPresent = false;
				for (Element el : governor2dependents.keySet())
					if (el.getTextContent().equals(governor.getTextContent())
							&& el.getAttributes().getNamedItem("idx").getNodeValue().equals(governor.getAttributes().getNamedItem("idx").getNodeValue())){
						isPresent=true;
						support = el;
					}
				if(isPresent)
					governor2dependents.get(support).add((Element) currentElement.getElementsByTagName("dependent").item(0));
				else {
					List<Element> nsubjDepGov = new ArrayList<>();
					nsubjDepGov.add((Element) currentElement.getElementsByTagName("dependent").item(0));
					governor2dependents.put(governor, nsubjDepGov);
				}
			}
		}
		return governor2dependents;
	}
	/**
	 * lowerPositionDependent ritorna la posizione minore di un governor e delle sue dipendendenze (di un certo tipo)
	 * @param nodes: lista delle dipendenze in formato XML
	 * @param governor: governor di partenza
	 * @param depType: dipendenza che si vuole analizzare
	 * @return finalPosition: la posizione minore di un governor e delle sue dipendendenze (di un certo tipo)
	 */
	private int lowerPositionDependent(NodeList nodes, Element governor, String depType) {
		int finalPosition = Integer.parseInt(governor.getAttributes().getNamedItem("idx").getNodeValue());
		Map<Element, List<Element>> governor2dependentsByCompound = governor2dependents(nodes, depType);
		for (Element el : governor2dependentsByCompound.keySet()) {
			if (el.getTextContent().equals(governor.getTextContent())
					&& el.getAttributes().getNamedItem("idx").getNodeValue().equals(governor.getAttributes().getNamedItem("idx").getNodeValue())){
				finalPosition = lowerPosition(governor2dependentsByCompound.get(el));
			}
		}
		return finalPosition;
	}
	/**
	 * 
	 * @param nodes
	 * @param position
	 * @return
	 */
	private Element getGovernorByPosition(NodeList nodes, int position) {
		Element element = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			Element currentElement = (Element) nodes.item(i);
			Element governor = (Element) currentElement.getElementsByTagName("governor").item(0);
			int elementPosition = Integer.parseInt(governor.getAttributes().getNamedItem("idx").getNodeValue());
			if (elementPosition==position)
				element = governor;
		}
		return element;
	}
	/**
	 * lowerPosition ritorna la posizione minore tra una lista di Element
	 * @param elements: lista degli Element in formato XML
	 * @return lowerPosition: posizione minore tra una lista di Element
	 */
	private int lowerPosition(List<Element> elements) {
		int finalPosition = Integer.parseInt(elements.get(0).getAttributes().getNamedItem("idx").getNodeValue());
		for (Element element : elements) {
			int elementPosition = Integer.parseInt(element.getAttributes().getNamedItem("idx").getNodeValue());
			if (elementPosition<finalPosition) {
				finalPosition = elementPosition;
			}
		}
		return finalPosition;
	}
	/**
	 * dependenciesFinder restituisce una lista per una specifica dipendenza dove ogni elemento è una coppia (dep,gov) 
	 * @param nodes: lista delle dipendenze in formato XML
	 * @param depType: dipendenza che si vuole cercare
	 * @return dependenciesList: lista per una specifica dipendenza dove ogni elemento è una coppia (dep,gov)
	 */
	private List<List<Element>> dependenciesFinder(NodeList nodes, String depType){
		List<List<Element>> dependenciesList = new LinkedList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currentNode = nodes.item(i);
			Element currentElement = (Element) nodes.item(i);
			String dep_type = currentNode.getAttributes().getNamedItem("type").getNodeValue();
			if(dep_type.contains(depType)){
				List<Element> dependencieDepGov = new ArrayList<>();
				dependencieDepGov.add((Element) currentElement.getElementsByTagName("dependent").item(0));
				dependencieDepGov.add((Element) currentElement.getElementsByTagName("governor").item(0));
				dependenciesList.add(dependencieDepGov);
			}
		}
		return dependenciesList;
	}
	/**
	 * 
	 * @param nodes
	 * @param node
	 * @return
	 */
	private List<Element> getAllPreviousElements(NodeList nodes, Element node) {
		List<Element> previousElements = new LinkedList<>();
		int inputPosition = Integer.parseInt(node.getAttributes().getNamedItem("idx").getNodeValue());

		for (int i = 0; i < nodes.getLength(); i++) {
			Element currentElement = (Element) nodes.item(i);
			
			Element previousElementDependent = (Element) currentElement.getElementsByTagName("dependent").item(0);
			Element previousElementGovernor = (Element) currentElement.getElementsByTagName("governor").item(0);

			int elementdepPosition = Integer.parseInt(previousElementDependent.getAttributes().getNamedItem("idx").getNodeValue());
			int elementgovPosition = Integer.parseInt(previousElementGovernor.getAttributes().getNamedItem("idx").getNodeValue());
			
			boolean sameNodeGovernor = inputPosition==elementgovPosition;
			boolean sameNodeDependent = inputPosition==elementdepPosition;

			if(sameNodeGovernor && (inputPosition>elementdepPosition))
				previousElements.add(previousElementDependent);
			if(sameNodeDependent && (inputPosition>elementgovPosition))
				previousElements.add(previousElementGovernor);
		}
		return previousElements;
	}
	
	/**
	 * 
	 * @param nodes
	 * @param node
	 * @return
	 */
	private List<Element> getPreviousElementsPointedByNode(NodeList nodes, Element node) {
		List<Element> previousElements = new LinkedList<>();
		int inputPosition = Integer.parseInt(node.getAttributes().getNamedItem("idx").getNodeValue());

		for (int i = 0; i < nodes.getLength(); i++) {
			Element currentElement = (Element) nodes.item(i);
			
			Element previousElementGovernor = (Element) currentElement.getElementsByTagName("governor").item(0);
			Element previousElementDependent = (Element) currentElement.getElementsByTagName("dependent").item(0);

			int previousPosition = Integer.parseInt(previousElementDependent.getAttributes().getNamedItem("idx").getNodeValue());
			int elementgovPosition = Integer.parseInt(previousElementGovernor.getAttributes().getNamedItem("idx").getNodeValue());

			boolean sameNode = ((inputPosition==elementgovPosition) && (previousElementGovernor.getTextContent().equals(node.getTextContent())));

			if(sameNode && (inputPosition>previousPosition))
				previousElements.add(previousElementDependent);
		}

		return previousElements;
	}
	/**
	 * 
	 * @param nodes
	 * @param node
	 * @return
	 */
	private List<Element> getNextElements(NodeList nodes, Element node){
		List<Element> nextElements = new LinkedList<>();
		int inputPosition = Integer.parseInt(node.getAttributes().getNamedItem("idx").getNodeValue());

		for (int i = 0; i < nodes.getLength(); i++) {
			Element currentElement = (Element) nodes.item(i);
			
			Element nextElementGovernor = (Element) currentElement.getElementsByTagName("governor").item(0);
			Element nextElementDependent = (Element) currentElement.getElementsByTagName("dependent").item(0);

			int nextPosition = Integer.parseInt(nextElementDependent.getAttributes().getNamedItem("idx").getNodeValue());
			int nodePosition = Integer.parseInt(node.getAttributes().getNamedItem("idx").getNodeValue());
			int elementgovPosition = Integer.parseInt(nextElementGovernor.getAttributes().getNamedItem("idx").getNodeValue());

			boolean sameNode = ((nodePosition==elementgovPosition) && (nextElementGovernor.getTextContent().equals(node.getTextContent())));

			if(sameNode && (inputPosition<nextPosition))
				nextElements.add(nextElementDependent);
		}

		return nextElements;
	}
	@Override
	// ritorna lista contentente n valori: frase, n-1 fatti
	public List<String> extractFacts(String phrase) {
		String dependenciesXML=null;
		Annotation document = new Annotation(phrase);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			dependenciesXML= sentence.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.XML);
		}
		try {
			/* es frammento XML
			 * <dep type="nsubj">
			 * 		<governor idx="8">supports</governor>
			 * 		<dependent idx="1">Eclipse</dependent>
			 * </dep>
			 */
			XMLPars x=new XMLPars(dependenciesXML);
			NodeList nodes = x.getNodesByTag("dep");
			int finalPosition = 0;
			Element finalElement = null;
			Map<Element, List<Element>> governor2dependentsByNmod = governor2dependents(nodes, "nmod:such_as");
			// ogni governor di un nmod va analizzato come frase
			for (Element governor : governor2dependentsByNmod.keySet()) {
				List<Element> dependents = governor2dependentsByNmod.get(governor);
				finalPosition = lowerPosition(dependents);
				System.out.println("primakosafai "+finalPosition);
				//verificare che finalElement è diverso da null?
				finalElement = getGovernorByPosition(nodes, finalPosition);
				finalPosition = lowerPositionDependent(nodes, finalElement, "compound");
				System.out.println("poikosafai "+finalPosition);
				
				System.out.println("partenza: "+finalElement.getTextContent());
				int startPosition = 0;
				for (Element el : getAllPreviousElements(nodes, finalElement)) {
					for (Element gov : governor2dependents(nodes, "nsubj").keySet()) {
						if (el.getTextContent().equals(gov.getTextContent())
								&& el.getAttributes().getNamedItem("idx").getNodeValue().equals(gov.getAttributes().getNamedItem("idx").getNodeValue())){
							Element dep = governor2dependents(nodes, "nsubj").get(gov).get(0);
							startPosition = Integer.parseInt(dep.getAttributes().getNamedItem("idx").getNodeValue());
						}
					}
					System.out.println(el.getTextContent());
				}
				System.out.println(startPosition);
				System.out.println("fine");
				
				// stampa frase
				for(int i = startPosition+1; i<finalPosition; i++){
					for (int j = 0; j < nodes.getLength(); j++) {
						Element currentElement = (Element) nodes.item(j);
						if(i==Integer.parseInt(currentElement.getElementsByTagName("governor").item(0).getAttributes().getNamedItem("idx").getNodeValue())){
							System.out.println(currentElement.getElementsByTagName("governor").item(0).getTextContent());
							break;
						}
						else if(i==Integer.parseInt(currentElement.getElementsByTagName("dependent").item(0).getAttributes().getNamedItem("idx").getNodeValue())){
							System.out.println(currentElement.getElementsByTagName("dependent").item(0).getTextContent());
							break;
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
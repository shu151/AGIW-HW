package extractor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import parser.XMLPars;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;


public class Extractor {
	private StanfordCoreNLP pipeline;
	public Extractor() {
		this.pipeline = new StanfordCoreNLP(
				PropertiesUtils.asProperties("annotators", "tokenize, ssplit, pos, lemma, ner, parse"));
	}
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
//		System.out.println("KKKK"+governor.getTextContent());
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
	 * 
	 * @param nodes
	 * @param position
	 * @return
	 */
	private Element getElementByPosition(NodeList nodes, int position) {
		Element element = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			Element currentElement = (Element) nodes.item(i);
			Element governor = (Element) currentElement.getElementsByTagName("governor").item(0);
			int governorPosition = Integer.parseInt(governor.getAttributes().getNamedItem("idx").getNodeValue());
			Element dependent = (Element) currentElement.getElementsByTagName("dependent").item(0);
			int dependentPosition = Integer.parseInt(governor.getAttributes().getNamedItem("idx").getNodeValue());
			if (governorPosition==position)
				element = governor;
			else if (dependentPosition==position)
				element = dependent;
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
//			System.out.println("EL DEP: "+previousElementDependent.getTextContent());
//			System.out.println("EL GOV: "+previousElementGovernor.getTextContent());
			boolean sameNodeGovernor = inputPosition==elementgovPosition;
			boolean sameNodeDependent = inputPosition==elementdepPosition;

			if(sameNodeGovernor && (inputPosition>elementdepPosition)){
				previousElements.add(previousElementDependent);
				System.out.println("EL PREC: "+previousElementDependent.getTextContent());
			}
			if(sameNodeDependent && (inputPosition>elementgovPosition)){
				previousElements.add(previousElementGovernor);
				System.out.println("EL PREC2: "+previousElementGovernor.getTextContent());
			}
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
	/**
	 * startPositionNsubj ritorna dato un certo elemento(parola) la posizione del suo soggetto
	 * @param nodes: lista delle dipendenze in formato XML
	 * @param finalElement: elemento di partenza da dove cercare il suo soggetto(nsubj)
	 * @return startPosition: posizione del soggetto
	 */
	private int startPositionNsubj(NodeList nodes, Element finalElement) {
		int startPosition = 0;
		int supPosition = 0;
		for (Element el : getAllPreviousElements(nodes, finalElement)) {
			Map<Element, List<Element>> governor2dependentsByNsubj = governor2dependents(nodes, "nsubj");
			for (Element gov : governor2dependentsByNsubj.keySet()) {

				if (finalElement.getTextContent().equals(gov.getTextContent())
						&& finalElement.getAttributes().getNamedItem("idx").getNodeValue().equals(gov.getAttributes().getNamedItem("idx").getNodeValue())){
					for (Element dep : governor2dependentsByNsubj.get(gov)) {
						supPosition = Integer.parseInt(dep.getAttributes().getNamedItem("idx").getNodeValue());
						if (supPosition>startPosition)
							startPosition = supPosition;
					}
				}
				else if (el.getTextContent().equals(gov.getTextContent())
						&& el.getAttributes().getNamedItem("idx").getNodeValue().equals(gov.getAttributes().getNamedItem("idx").getNodeValue())){
					for (Element dep : governor2dependentsByNsubj.get(gov)) {
						supPosition = Integer.parseInt(dep.getAttributes().getNamedItem("idx").getNodeValue());
						if (supPosition>startPosition)
							startPosition = supPosition;
					}
					System.out.println("CIARRIVA");
				}
				else{
					startPosition = startPositionNsubj(nodes,el);
					System.out.println("CIARRIVA 2");
				}
			}
		}
		return startPosition;
	}
	/**
	 * depTypeNmodList restituisce una lista delle tipologie di nmod presenti nella frase
	 * @param nodes: lista delle dipendenze in formato XML
	 * @return depType: lista delle tipologie di nmod presenti nella frase
	 */
	private Set<String> depTypeNmodList(NodeList nodes) {
		Set<String> depType = new HashSet<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currentNode = nodes.item(i);
			String dep_type = currentNode.getAttributes().getNamedItem("type").getNodeValue();
			if (dep_type.contains("nmod")){
				depType.add(dep_type);
			}
		}
		return depType;
	}
		
	public List<String> extractFacts(List<String> filtPhraseWithEntities) {
		
		List<String> entity = new ArrayList<>();
		for (int i = 1; i<filtPhraseWithEntities.size() ;i++) {
			entity.add(filtPhraseWithEntities.get(i));
		}
		String filtphrase = filtPhraseWithEntities.get(0);
		Map<String,List<List<String>>> relationsMap = new HashMap<>();
		List<String> relations = new ArrayList<>();
		String dependenciesXML=null;
		Annotation document = new Annotation(filtphrase);
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
			Set<String> typeNmod = depTypeNmodList(nodes);
			for (String type : typeNmod) {
				Map<Element, List<Element>> governor2dependentsByNmod = governor2dependents(nodes, type);
				for(Element e : governor2dependentsByNmod.keySet()){
					System.out.println("VEDIAMO "+e.getTextContent());
				}
				// ogni governor di un nmod va analizzato come frase
				for (Element governor : governor2dependentsByNmod.keySet()) {
					List<String> dependentsNmodName = new ArrayList<>();
					// per ora aggiunge solo un soggetto
					List<String> dependentsNsubjName = new ArrayList<>();
					List<Element> dependents = governor2dependentsByNmod.get(governor);
					if(dependents.size()>1){
						for(Element e : dependents){
							dependentsNmodName.add(e.getTextContent());
							System.out.println("VEDIAMO DEPEND "+e.getTextContent());
						}
						finalPosition = lowerPosition(dependents);
						System.out.println("POSIZIONE PROVVISORIA "+finalPosition);
						//verificare che finalElement è diverso da null?si perche in finalPosition potremmo
						//avere solo dependent e nessun governor
						finalElement = getGovernorByPosition(nodes, finalPosition);
						if (finalElement!=null){
							finalPosition = lowerPositionDependent(nodes, finalElement, "compound");
							System.out.println("ELEMENTO PROVVISORIO: "+finalElement.getTextContent());
						}
						System.out.println("ggg "+governor.getTextContent());
						int startPosition = startPositionNsubj(nodes, governor);
						dependentsNsubjName.add(getElementByPosition(nodes, startPosition).getTextContent());
						System.out.println("POSIZIONE INIZIALE: "+startPosition);
						System.out.println("POSIZIONE FINALE: "+finalPosition);
						System.out.println("fine");
						
						// stampa frase
						System.out.println("INIZIO FRASE");
						List<String> relationProv = new ArrayList<>();
						for(int i = startPosition+1; i<finalPosition; i++){
							for (int j = 0; j < nodes.getLength(); j++) {
								Element currentElement = (Element) nodes.item(j);
								if(i==Integer.parseInt(currentElement.getElementsByTagName("governor").item(0).getAttributes().getNamedItem("idx").getNodeValue())){
									relationProv.add(currentElement.getElementsByTagName("governor").item(0).getTextContent());
									break;
								}
								else if(i==Integer.parseInt(currentElement.getElementsByTagName("dependent").item(0).getAttributes().getNamedItem("idx").getNodeValue())){
									relationProv.add(currentElement.getElementsByTagName("dependent").item(0).getTextContent());
									break;
								}
							}
						}
						String relation = "";
						for (String word : relationProv) {
							relation = relation+word+" ";
						}
						List<String> entityListDep = new ArrayList<>();
						for (String nameDepNmod : dependentsNmodName) {
							for (String nameEntity : entity){
								if (nameEntity.contains(nameDepNmod))
									entityListDep.add(nameEntity);
							}
						}
						List<String> entityListNsubj = new ArrayList<>();
						for (String nameDepNsubj : dependentsNsubjName) {
							for (String nameEntity : entity){
								if (nameEntity.contains(nameDepNsubj))
									entityListNsubj.add(nameEntity);
							}
						}
						List<List<String>> subjDep = new ArrayList<>();
						relationsMap.put(relation, subjDep);
						relations.add(relation);
						System.out.println(relation);
						System.out.println("FINE FRASE");
					}
				}
				System.out.println("FINE FOR");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return relations;
	}
}
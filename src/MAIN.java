import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.ConnectedComponents.ConnectedComponent;
import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.xml.sax.SAXException;

import classification.GraphGenerator;
import classification.SimilarityMeasure;
import classification.TrivialSimiliratyMeasure;
import factory.QuestionFactory;
import model.Question;


public class MAIN {
	
	public static ArrayList<String> extractKeyword(String[] keywords, String text) {
		ArrayList<String> listKeyword = new ArrayList<String>();
		for (String keyword : keywords) {
			if (keyword.length() > 2) {
				if (text.toLowerCase().contains(keyword)) {
					listKeyword.add(keyword);
				}
			}
		}
		return listKeyword;
	}
	
	public static void saveObjectToFile(String nameFile, Object object) throws IOException {
		FileOutputStream fos = new FileOutputStream(nameFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
		oos.close();
	}
	
	public static Object loadObjectFromFile(String nameFile) throws ClassNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(nameFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object o = ois.readObject();
		ois.close();
		return o;
	}	
	
	public static void connectedComponentSize(SingleGraph graph) {
		int cpt;
		ConnectedComponents ccs = new ConnectedComponents();
		ccs.init(graph);
		ccs.setCountAttribute("idComponent");
		System.out.println(ccs.getConnectedComponentsCount() + " composante(s) connexe(s) | C:"+ccs.getGiantComponent().size());

		int[] kkKount = new int[ccs.getGiantComponent().size()]; 
		Iterator<ConnectedComponents.ConnectedComponent> it = ccs.iterator();
		while (it.hasNext()) {
			ConnectedComponents.ConnectedComponent cc = it.next();
			Iterator<Node> itNode = cc.iterator();
			cpt = 0;
			while (itNode.hasNext()) {
				itNode.next();
				cpt++;
			}
			if (cpt != 0) {
				kkKount[cpt-1]++;
			}
		}
		
		cpt = 0;
		for (int i = 0; i < kkKount.length; i++) {
			if (kkKount[i] != 0) {
				cpt += kkKount[i]*(i+1);
				System.out.println("Il y a "+kkKount[i]+" composante(s) connexe(s) de taille "+ (i+1));
			}
		}
		System.out.println("J'ai calculé et il y a "+cpt+" node(s)");
	}

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException, ParserConfigurationException, SAXException, ElementNotFoundException, GraphParseException {
		
		System.out.println("Begin");
		long startTime = System.nanoTime();
		long time = startTime;
		
		int minKeyword = 5;
		double rate = 0.6;		
		
		//---------------------------------GET QUESTIONS---------------------------------
		//---------------------------------GET TF-IDF (XML)---------------------------------
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document doc = builder.parse("tf-idf_pre.xml");
//		System.err.println("Parser xml : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
//		time = System.nanoTime();
		
		
		//---------------------------------GET KEYWORDS---------------------------------
//		NodeList nodeListTerm = doc.getElementsByTagName("name_term");
//		String[] keywords = new String[nodeListTerm.getLength()];
//		for (int i=0; i < nodeListTerm.getLength(); i++) {
//			keywords[i] = nodeListTerm.item(i).getTextContent();
//		}
//		System.err.println("Keyword gen : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
//		time = System.nanoTime();

		
		//---------------------------------EXTRACT KEYWORDS FROM QUESTIONS---------------------------------
//		for (int id : questions.keySet()) {
//			questionsKeyword.put(id, extractKeyword(keywords, questions.get(id).getTitle()+questions.get(id).getBody()));
//		}
//		System.err.println("question keyword : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
//		time = System.nanoTime();
		
		
		//---------------------------------SELECTION QUESTION KEYWORD---------------------------------
//		HashMap<Integer, ArrayList<String>> tmpSer = new HashMap<Integer, ArrayList<String>>();
//		for (int id : questionsKeyword.keySet()) {
//			if (questionsKeyword.get(id).size() >= minKeyword) {
//				tmpSer.put(id, questionsKeyword.get(id));
//			}
//		}
		//---------------------------------SAVE INTO FILE---------------------------------
//		saveObjectToFile("questionsKeywords_pre.ser", tmpSer);
		
		//---------------------------------GET QUESTIONS KEYWORDS---------------------------------
		HashMap<Integer, ArrayList<String>> questionsKeywords = (HashMap<Integer, ArrayList<String>>) loadObjectFromFile("questionsKeywords_pre.ser");
		System.err.println("Get questionKeyword : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
		time = System.nanoTime();
//		
//		//---------------------------------PROCESS CLASSES---------------------------------
//		
		SingleGraph graph = new SingleGraph("Graphes des questions transformées en liste de keywords");
		graph.read("graph-0.6.dgs");
//		
//		for (int id : questionsKeywords.keySet()) {
//			graph.addNode(Integer.toString(id));
//		}
//
		SimilarityMeasure<ArrayList<String>> similarityMeasure = new TrivialSimiliratyMeasure(questionsKeywords, rate);
		int cpt = 0;
//		int nbATraite = 1000;
//		int onePercent = nbATraite/100;
//		Question question;
//		for (int id : questionsKeywords.keySet()) {
//			question = questions.get(id);
//			if (cpt%onePercent==0) {
//				System.out.print(cpt/onePercent+"% \r");
//			}
//			int idMostSimilar = similarityMeasure.getMostSimilarQuestion(question);
//			if (idMostSimilar != -1) {
//				graph.addEdge(id+"."+idMostSimilar, Integer.toString(id), Integer.toString(idMostSimilar), true);
//				
//			}
//			cpt++;
//			if (cpt >= nbATraite) {
//				break;
//			}
//		}
//		for (int id : questionsKeywords.keySet()) {
//			Node n = graph.getNode(Integer.toString(id));
//			if(n.getDegree()==0)
//				graph.removeNode(n.getIndex());
//		}
		
		connectedComponentSize(graph);

		
		ConnectedComponents ccs = new ConnectedComponents();
		ccs.init(graph);
		ccs.setCountAttribute("idComponent");
		System.out.println(ccs.getConnectedComponentsCount() + " composante(s) connexe(s) | C:"+ccs.getGiantComponent().size());

		int[] kkKount = new int[ccs.getGiantComponent().size()]; 
		Iterator<ConnectedComponents.ConnectedComponent> it = ccs.iterator();
		cpt = 0;
		while (it.hasNext()) {
			boolean similar = true;
			ConnectedComponents.ConnectedComponent cc = it.next();
			Iterator<Node> itNode = cc.iterator();
			while (itNode.hasNext() && similar) {
				Node n1 = itNode.next();
				Iterator<Node> itNode2 = cc.iterator();
				while (itNode2.hasNext() && similar) {
					Node n2 = itNode2.next();
					if (similarityMeasure.getSimiliratyMeasure(questionsKeywords.get(Integer.parseInt(n1.getId())), questionsKeywords.get(Integer.parseInt(n2.getId()))) != -1) {
						similar = false;
					}
				}
			}
			if (similar) {
				cpt++;
			}
		}
		
		//---------------------------------PROCESS CLASSES---------------------------------
		System.out.println("\n\nIl y a "+cpt+" composante(s) qui passe le deuxième test donc c'est bon vous pouvez rentrer chez vous ;)");
		//graph.display();
		
		System.err.println("Graph Process : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
		time = System.nanoTime();
		
		
		/*
		System.out.println("-------------------------------------------------------");
		System.err.println("\n\nTermine en "+(float)(System.nanoTime() - startTime)/1000000000+" secondes");
//		System.out.println(questions.size()+" questions sont dans 'questions'");
		System.out.println("-------------------------------------------------------");
		*/
		
		

	}

}

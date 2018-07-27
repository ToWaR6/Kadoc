import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import classification.TFIDF;
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

		int[] ccCount = new int[ccs.getGiantComponent().size()]; 
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
				ccCount[cpt-1]++;
			}
		}
		
		cpt = 0;
		for (int i = 0; i < ccCount.length; i++) {
			if (ccCount[i] != 0) {
				cpt += ccCount[i]*(i+1);
				System.out.println("Il y a "+ccCount[i]+" composante(s) connexe(s) de taille "+ (i+1));
			}
		}
		System.out.println("Il y a "+cpt+" node(s)");
	}
	
	public static Document parseXML(String pathfile) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(pathfile);
	}

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException, ParserConfigurationException, SAXException, ElementNotFoundException {
		
		System.out.println("Begin");
		long startTime = System.nanoTime();
		long time = startTime;
			
		String TFIDFpath = args[0]+File.separator+"TFIDF"+File.separator;
		String XMLpath = args[0]+File.separator+"xml"+File.separator;
		String QUESTIONpath = args[0]+File.separator+"question"+File.separator;

		HashMap<Integer, Question> mapQuestion = (HashMap<Integer, Question>) loadObjectFromFile(QUESTIONpath+"questions.ser");
		System.out.println("Get questions : "+(float)(System.nanoTime() - time)/1000000000+" ("+(float)(System.nanoTime()-startTime)/1000000000+") seconde(s)");
		time = System.nanoTime();
		
		Set<String> whiteListKeywords = new HashSet<String>();
		String[] multiWordTerm;
		Document doc;
		NodeList nameTerms;
		
		doc = parseXML(XMLpath+"listMulti-termWord.xml");
		nameTerms = doc.getElementsByTagName("name_term");
		multiWordTerm = new String[nameTerms.getLength()];
		for (int i=0; i<nameTerms.getLength(); i++) {
			String multTerm = nameTerms.item(i).getTextContent();
			multiWordTerm[i] = multTerm;
			String singleTerm = multTerm.replaceAll(" ", "_");
			whiteListKeywords.add(singleTerm);
			
		}
		
		doc = parseXML(XMLpath+"tf-idf_pre-code.xml");
		nameTerms = doc.getElementsByTagName("name_term");
		for (int i=0; i<nameTerms.getLength(); i++) {
			if (nameTerms.item(i).getTextContent().length() >= 3) {
				String singleTerm = nameTerms.item(i).getTextContent().replaceAll(" ", "_");
				whiteListKeywords.add(singleTerm);
			}
		}
		
		System.out.println("Get xml files : "+(float)(System.nanoTime() - time)/1000000000+" ("+(float)(System.nanoTime()-startTime)/1000000000+") seconde(s)");
		time = System.nanoTime();
		
		TFIDF tfidf = new TFIDF(whiteListKeywords.toArray(new String[0]), multiWordTerm);
		
		HashMap<Integer, HashMap<String, Double>> mapScore = tfidf.calculate(mapQuestion);
		
		System.out.println("Calcul tfidf : "+(float)(System.nanoTime() - time)/1000000000+" ("+(float)(System.nanoTime()-startTime)/1000000000+") seconde(s)");
		time = System.nanoTime();
		
		tfidf.generateArffFile(TFIDFpath+"tfidf.arff");
		System.out.println("Generate arff file : "+(float)(System.nanoTime() - time)/1000000000+" ("+(float)(System.nanoTime()-startTime)/1000000000+") seconde(s)");
		time = System.nanoTime();
		
		System.out.println("-------------------------------------------------------");
		System.err.println("\n\nTermine en "+(float)(System.nanoTime() - startTime)/1000000000+" secondes");
		System.out.println("-------------------------------------------------------");

	}

}
